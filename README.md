# Spectro-Based Oil Adulteration Detection System

## Overview
This project is an end-to-end system for detecting adulteration in edible oils using spectral sensor data and machine learning.

A spectral sensor is used to capture the light absorption/reflection characteristics of oil samples.
These spectral readings are used to train a machine learning model, which is then integrated into an Android application for real-time prediction.

---

## Key Idea
Different oils have unique spectral signatures. Adulterated oils show variations in these patterns.  
The system learns these patterns and classifies oil samples as pure or adulterated.

---

## System Architecture
1. Spectral sensor captures oil sample data
2. Data is collected and stored as dataset
3. Machine learning model is trained using this data
4. Trained model is integrated into Android app
5. App predicts adulteration for new samples

---

## Features
- Uses real spectral sensor data (not just images)
- Machine learning-based classification
- Android app for user-friendly prediction
- End-to-end pipeline from data collection to deployment

---

## Tech Stack
- Android Studio (Java)
- Python (Google Colab)
- Machine Learning (classification model)
- Spectral Sensor (hardware data acquisition)

---

## Project Structure
- android-app/ → Mobile application
- model-training/ → Colab notebook and training code
- dataset/ → Sample spectral dataset

---

## How It Works
- Spectral sensor captures intensity values across wavelengths
- Data is preprocessed and fed into ML model
- Model learns patterns of pure vs adulterated oils
- Android app uses trained model to predict new samples

---

## Future Improvements
- Improve model accuracy with larger dataset
- Cloud-based model deployment
- Support for multiple oil types

---

## Author
Kartik , Abdullah , Ajay ,Shrutika 
