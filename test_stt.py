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
        test_text = "이 서비스는 사용자의 경험을 극대화하기 위해 AI 기술을 활용하여 데이터를 분석하고 최적의 추천을 제공합니다. 이를 통해 사용자는 더욱 직관적이고 효율적인 방식으로 원하는 정보를 얻을 수 있습니다."
        output_file = "test_output.mp3"
        text_to_speech(test_text, output_file)
        print(f"테스트 완료! 오디오가 {output_file}에 저장되었습니다.")
        sys.exit(0)
