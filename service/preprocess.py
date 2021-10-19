import librosa
import numpy as np
from matplotlib import cm

DURATION_AUDIO = 5
SAMPLE_RATE = 16000
MAX_LENGTH = DURATION_AUDIO * SAMPLE_RATE

def wave_to_spectrogram(filepath):
    signal, sr = librosa.load(filepath, sr=None)
    spectrogram = create_spectrogram(signal, sr)
    spectrogram = spectrogram / 255
    spectrogram = spectrogram[np.newaxis, ...]
    return spectrogram

def pad_signal(signal):
  if signal.shape[0] > MAX_LENGTH:
      return signal[:MAX_LENGTH]
  elif signal.shape[0] < MAX_LENGTH:
      padded = np.zeros((MAX_LENGTH,), dtype=np.float32)
      padded[0:signal.shape[0]] = signal
      return padded
  else:
      return signal

def create_spectrogram(signal, sr):
  signal = pad_signal(signal)
  s = librosa.feature.melspectrogram(signal, sr=sr)
  s = librosa.power_to_db(s)

  my_cm = cm.get_cmap('viridis')
  normed_data = (s - np.min(s)) / (np.max(s) - np.min(s))
  img = (255 * my_cm(normed_data)).astype('uint8')
  img = np.delete(img[:][:], 3, axis=2)
  
  return img