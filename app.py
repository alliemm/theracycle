from flask import Flask, request, jsonify
from gemini_engine import TheraCycleEngine

app = Flask(__name__)
engine = TheraCycleEngine()

@app.route('/analyze', methods=['POST'])
def analyze_session():
    data = request.json
    raw_text = data.get('notes')
    
    if not raw_text:
        return jsonify({"error": "No notes provided"}), 400
    
    # triggers AI distillation
    analysis = engine.distill_patient_session(raw_text)
    
    return jsonify({
        "status": "success",
        "analysis": analysis
    })

if __name__ == '__main__':
    app.run(debug=True)
