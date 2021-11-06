package com.example.studyapp

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.example.studyapp.databinding.ActivityTranslator1Binding
import com.google.firebase.ml.naturallanguage.FirebaseNaturalLanguage
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslateLanguage
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslator
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslatorOptions
import java.lang.Exception
import java.util.*

class translator1 : AppCompatActivity() {

    private val REQUEST_CODE_SPEECH_INPUT =100

    lateinit var mTTs: TextToSpeech   // TTS


    lateinit var binding: ActivityTranslator1Binding
    var option : FirebaseTranslatorOptions.Builder? = null
    var translator : FirebaseTranslator? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_translator1)
        option = FirebaseTranslatorOptions.Builder()







        binding.next.setOnClickListener{
            val intent = Intent(this, ocr::class.java)
            startActivity(intent)
        }












        // 보이스 버튼(mic) 음성 인식
        binding.voiceBtn.setOnClickListener {
            speak();
        }










        // TTS 기능 (SPEAK 버튼 + STOP 버튼)

        mTTs = TextToSpeech(applicationContext, TextToSpeech.OnInitListener {status ->
            if (status != TextToSpeech.ERROR) {
                //
                mTTs.language = Locale.UK
            }
        })

        binding.speakBtn.setOnClickListener {
            val toSpeak = binding.inputText.text.toString()
            if (toSpeak == "") {
                Toast.makeText(this, "Enter text", Toast.LENGTH_SHORT).show()
            }
            else {
                Toast.makeText(this, toSpeak, Toast.LENGTH_SHORT).show()
                mTTs.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null)
            }
        }



        binding.stopBtn.setOnClickListener {
            if (mTTs.isSpeaking) {

                mTTs.stop()

            }
            else {
                Toast.makeText(this, "Not speaking", Toast.LENGTH_SHORT).show()

            }
        }










































        // 번역기 (한국어 -> 영어 & 영어 -> 한국어) 시피너로 언어 설정

        binding.inputText.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(p0: Editable?) {
                translator?.translate(p0.toString())?.addOnSuccessListener {
                    binding.outTextView.text = it
                }
            }
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }



        })
        // 왼쪽 문자 입력(id = inputText)
        binding.fromSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                if(p2 == 0){
                    option?.setSourceLanguage(FirebaseTranslateLanguage.EN)  // 번역 언어 설정 .영어
                }else if(p2 == 1){
                    option?.setSourceLanguage(FirebaseTranslateLanguage.KO)  //              .한국어
                }
            }


        }
        // 오른쪽 번역문 출력(id = outTextView)
        binding.toSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                if(p2 == 0){
                    option?.setTargetLanguage(FirebaseTranslateLanguage.EN)  // 번역될 언어 설정 .영어
                }else if(p2 == 1){
                    option?.setTargetLanguage(FirebaseTranslateLanguage.KO)  //              .한국어
                }
                translator = FirebaseNaturalLanguage.getInstance().getTranslator(option!!.build()) // 실제 번역을 수행
                translator?.downloadModelIfNeeded()           // 번역 언어를 다운로드
            }


        }

    }

















    // STT 실행 함수
    private fun speak() {
        val mIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        mIntent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        mIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        mIntent.putExtra(RecognizerIntent.EXTRA_PROMPT, "음성을 인식중입니다 !")

        try {
            startActivityForResult(mIntent, REQUEST_CODE_SPEECH_INPUT)
        }
        catch (e: Exception) {
            Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode) {
            REQUEST_CODE_SPEECH_INPUT -> {
                if (resultCode == Activity.RESULT_OK && null != data) {

                    val result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                    binding.inputText.setText(result.toString())  // 음성 -> 문자로 변환

                }
            }
        }
    }



















    override fun onPause() {           // TTS 스톱 버튼 실행
        if (mTTs.isSpeaking) {

            mTTs.stop()

        }
        super.onPause()
    }
}