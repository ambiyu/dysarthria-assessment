from flask import Flask, request, jsonify
from tensorflow import keras
from preprocess import wave_to_spectrogram
import numpy as np
import random
import os

app = Flask(__name__)
PORT = 1234

model = keras.models.load_model("model.h5")
mappings = ["low", "mid", "high"]

if not os.path.isdir('audio_files'):
  os.mkdir('audio_files')

@app.route('/predict', methods=['POST'])
def make_prediction():
  audio_file = request.files['audio_file']

  # Save file locally so that librosa can load it
  filename = str(random.randint(0, 1000)) + '.wav'
  filepath = os.path.join("audio_files", filename)
  audio_file.save(filepath)

  spectrogram = wave_to_spectrogram(filepath=filepath)

  prediction = model.predict(spectrogram)
  predicted_index = np.argmax(prediction)

  data = {
    "index": int(predicted_index),
    "intelligibility": mappings[predicted_index]
  }

  os.remove(filepath)

  return jsonify(data)


if __name__ == "__main__":
  app.run(port=PORT)