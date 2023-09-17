from flask import Flask, request
model_path = "./model"
from transformers import pipeline
sentiment_task = pipeline("text-classification", model=model_path, tokenizer= model_path, device_map="cpu")
app = Flask(__name__)


@app.route("/" , methods=["POST"])
def get_sentiment():
    opinion = request.get_json()
    # print(type(opinion))
    sentiment = sentiment_task(opinion['opinion'])
    score = sentiment[0]['score']
    print(sentiment)
    print(score)
    label = sentiment[0]['label']
    print(label)
    if label == 'negative':
        score = -score
        print('negative')
    elif label == 'positive':
        score = score
        print('positive')
    else:
        score = 0.0
        print('neutral')
    return [score]

if __name__ == "__main__":
    app.run()
