import sys
import pyttsx3

def text_to_speech(text, output_file):
    engine = pyttsx3.init()
    engine.setProperty('rate', 150)
    engine.setProperty('volume', 0.9)
    
    voices = engine.getProperty('voices')
    if len(voices) > 0:
        # 한국어 음성이 있으면 선택
        for voice in voices:
            if 'korean' in voice.id.lower():
                engine.setProperty('voice', voice.id)
                break
    
    engine.save_to_file(text, output_file)
    engine.runAndWait()
    return output_file

if __name__ == "__main__":
    if len(sys.argv) >= 3:
        text = sys.argv[1]
        output_path = sys.argv[2]
        text_to_speech(text, output_path)
        print(f"TTS 완료: {output_path}")
        sys.exit(0)
    else:
        # 테스트 문장 추가 - 인자가 없을 때 실행할 예제
        print("명령줄 인자가 없어 기본 테스트를 실행합니다.")
        test_text = "스토블릭스는 AI 기반 독서 숏폼 플랫폼입니다. 책 속 문장 하나하나가 디지털 기념비처럼 기억되고, AI를 통해 숏폼 영상으로 재탄생하는 곳입니다."
        output_file = "test_output.mp3"
        text_to_speech(test_text, output_file)
        print(f"테스트 완료! 오디오가 {output_file}에 저장되었습니다.")
        sys.exit(0)