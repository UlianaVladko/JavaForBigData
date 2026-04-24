"""
Семинар 8: SparkSQL-решение
Задача: выручка по продуктам (та же задача, что и в KSQL)

Запуск:
  docker compose exec spark spark-submit \
    --packages org.apache.spark:spark-sql-kafka-0-10_2.12:3.5.0 \
    /spark/analysis.py
"""

from pyspark.sql import SparkSession
from pyspark.sql.functions import from_json, col, count, sum, avg
from pyspark.sql.types import StructType, StructField, StringType, IntegerType, DoubleType

spark = SparkSession.builder \
    .appName("seminar8-orders-analysis") \
    .getOrCreate()

schema = StructType([
    StructField("orderId", StringType(), True),
    StructField("product", StringType(), True),
    StructField("quantity", IntegerType(), True),
    StructField("price", DoubleType(), True),
    StructField("timestamp", IntegerType(), True)
])

# TODO 13: Прочитайте данные из Kafka-топика "orders" в batch-режиме
# Подсказка:
# df = spark.read.format("kafka") \
#     .option("kafka.bootstrap.servers", "kafka:29092") \
#     .option("subscribe", "orders") \
#     .option("startingOffsets", "earliest") \
#     .load()
df = spark.read.format("kafka") \
    .option("kafka.bootstrap.servers", "kafka:29092") \
    .option("subscribe", "orders") \
    .option("startingOffsets", "earliest") \
    .load()

orders = df.select(
    from_json(col("value").cast("string"), schema).alias("data")
).select("data.*")

orders = orders.withColumn("event_time", col("timestamp"))

orders.createOrReplaceTempView("orders")

    # TODO 14: Напишите SparkSQL-запрос для выручки по продуктам
    # SELECT product, COUNT(*), SUM(price * quantity), AVG(price)
    # FROM orders GROUP BY product ORDER BY total_revenue DESC
print("===== SPARK SQL RESULT =====")

sql_result = spark.sql("""
    SELECT
        product,
        COUNT(*) AS order_count,
        SUM(price * quantity) AS total_revenue,
        AVG(price) AS avg_price
    FROM orders
    GROUP BY product
    ORDER BY total_revenue DESC
""")

sql_result.show()

    # TODO 15: Напишите тот же запрос через DataFrame API
    # orders.groupBy("product").agg(count("*"), sum(col("price") * col("quantity")))
print("===== DATAFRAME API RESULT =====")

df_result = orders.groupBy("product").agg(
    count("*").alias("order_count"),
    sum(col("price") * col("quantity")).alias("total_revenue"),
    avg("price").alias("avg_price")
).orderBy(col("total_revenue").desc())

df_result.show()

spark.stop()