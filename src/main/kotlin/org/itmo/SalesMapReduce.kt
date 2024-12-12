package org.itmo

import java.io.DataInput
import java.io.DataOutput
import java.math.BigDecimal
import java.math.RoundingMode
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.Path
import org.apache.hadoop.io.Text
import org.apache.hadoop.io.Writable
import org.apache.hadoop.io.WritableComparable
import org.apache.hadoop.io.WritableComparator
import org.apache.hadoop.mapreduce.Job
import org.apache.hadoop.mapreduce.Mapper
import org.apache.hadoop.mapreduce.Reducer
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat
import kotlin.system.exitProcess

class SalesMapReduce {
    class SalesMapper : Mapper<Any, Text, Text, SalesWritable>() {
        override fun map(key: Any, value: Text, context: Context) {
            val fields = value.toString().split(",")

            // Skip header and malformed lines
            if (fields.size >= 5 && fields[0] != "transaction_id") {
                val category = fields[2]
                val price = fields[3].toDoubleOrNull() ?: 0.0
                val quantity = fields[4].toIntOrNull() ?: 0

                val revenue = truncatePrice(price * quantity)
                context.write(Text(category), SalesWritable(revenue, quantity))
            }
        }

        private fun truncatePrice(d: Double): Double {
            return BigDecimal.valueOf(d)
                .setScale(2, RoundingMode.UP)
                .toDouble()
        }
    }

    class SalesReducer : Reducer<Text, SalesWritable, Text, Text>() {
        override fun reduce(key: Text, values: Iterable<SalesWritable>, context: Context) {
            var totalRevenue = 0.0
            var totalQuantity = 0

            for ((revenue, quantity) in values) {
                totalRevenue += revenue
                totalQuantity += quantity
            }

            val resultingRevenue = BigDecimal.valueOf(totalRevenue).setScale(2, RoundingMode.UP)

            context.write(key, Text("$resultingRevenue\t$totalQuantity"))
        }
    }

    //class RevenueComparator : WritableComparator(SalesWritable::class.java, true) {
    //    //No-op.
    //}

    data class SalesWritable(
        var revenue: Double = 0.0,
        var quantity: Int = 0
    ) : Writable { //WritableComparable<SalesWritable> {
        override fun write(out: DataOutput) {
            out.writeDouble(revenue)
            out.writeInt(quantity)
        }

        override fun readFields(input: DataInput) {
            revenue = input.readDouble()
            quantity = input.readInt()
        }

        //override fun compareTo(other: SalesWritable?): Int {
        //    return other?.revenue?.compareTo(this.revenue) ?: 0
        //}
    }
}

fun main(args: Array<String>) {
    val conf = Configuration()

    val job = Job.getInstance(conf, "Sales Analysis")

    job.setJarByClass(SalesMapReduce::class.java)
    job.mapperClass = SalesMapReduce.SalesMapper::class.java
    job.reducerClass = SalesMapReduce.SalesReducer::class.java
    job.mapOutputKeyClass = Text::class.java
    job.mapOutputValueClass = SalesMapReduce.SalesWritable::class.java
    job.outputKeyClass = Text::class.java
    job.outputValueClass = Text::class.java

    //job.setSortComparatorClass(SalesMapReduce.RevenueComparator::class.java)

    FileInputFormat.addInputPath(job, Path(args[0]))
    FileOutputFormat.setOutputPath(job, Path(args[1]))

    exitProcess(if (job.waitForCompletion(true)) 0 else 1)
}
