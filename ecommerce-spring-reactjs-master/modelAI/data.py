import pandas as pd
import psycopg2

conn = psycopg2.connect(database = "TestDB", 
                        user = "postgres", 
                        host= 'localhost',
                        password = "root",
                        port = 5432)

cur = conn.cursor()
cur.execute('SELECT * FROM perfume_reviews;')
perfume_reviews = cur.fetchall()
conn.commit()

perfume_reviews_df = pd.DataFrame(perfume_reviews, columns=['perfume_id', 'review_id'])

cur = conn.cursor()
cur.execute('SELECT * FROM review;')
review = cur.fetchall()
conn.commit()

review_df = pd.DataFrame(review, columns=['id', 'author', 'message', 'rating', 'date'])

cur = conn.cursor()
cur.execute('SELECT * FROM order_item;')
item = cur.fetchall()
conn.commit()
conn.close()

item_df = pd.DataFrame(item, columns=['id', 'amount', 'quantity', 'perfume_id'])
item_df = item_df.groupby(['perfume_id'])['quantity'].sum()
item_df = item_df.reset_index(name='quantity_sum', drop=False)