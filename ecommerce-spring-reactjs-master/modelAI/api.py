from flask import Flask, request
from flask_restful import Resource, Api
from concat import author2id, merged_df
import numRate
from popularity import popular_perfume_id
from flask_cors import CORS
import model
from model import df, svd

# Initialize Flask app
app = Flask(__name__)
api = Api(app)

class Recommend(Resource):
    def get(self, author):
        user_id = author2id.get(author)
        nRate = numRate.count_perfumes_for_author(merged_df, user_id)
        if (nRate < 3):
            return popular_perfume_id
        else:
            return model.recommend_items(user_id, svd, df).tolist()
        
api.add_resource(Recommend, '/recommend/<string:author>')
CORS(app)

if __name__ == '__main__':
    app.run(debug=True, host='0.0.0.0', port=8000)
