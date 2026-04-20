import google.generativeai as genai
import os

class TheraCycleEngine:
    def __init__(self):
        # Change to environment variable in production
        self.api_key = os.getenv("GEMINI_API_KEY")
        genai.configure(api_key=self.api_key)
        self.model = genai.GenerativeModel('gemini-pro')

    def distill_patient_session(self, raw_transcript):
        """
        Takes raw, unstructured patient notes and converts them 
        into objective clinical observations.
        """
        prompt = f"""
        Act as a clinical documentation assistant. 
        Analyze the following therapist notes and extract:
        1. Observed Sentiment (Scale 1-10)
        2. Key Behavioral Themes
        3. Objective Summary (Remove subjective bias)

        Raw Notes: {raw_transcript}
        """
        
        response = self.model.generate_content(prompt)
        return response.text
