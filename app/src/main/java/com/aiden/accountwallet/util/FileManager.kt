package com.aiden.accountwallet.util

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.os.Environment
import android.widget.Toast
import com.aiden.accountwallet.R
import com.aiden.accountwallet.data.dto.Info
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import java.io.BufferedReader
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.nio.charset.StandardCharsets
import java.text.SimpleDateFormat
import java.util.Date

object FileManager {

    private fun execute(command: String): String {
        val process = Runtime.getRuntime().exec(command)
        val reader = BufferedReader(InputStreamReader(process.inputStream))
        val output = StringBuilder()
        var line: String?
        while (reader.readLine().also { line = it } != null) {
            output.append(line).append("\n")
        }
        return output.toString()
    }

    private fun readRawTextFile(context: Context, resId: Int): String {
        val inputStream = context.resources.openRawResource(resId)
        val reader = BufferedReader(InputStreamReader(inputStream))
        val stringBuilder = StringBuilder()
        var line: String?
        try {
            while (reader.readLine().also { line = it } != null) {
                stringBuilder.append(line)
                stringBuilder.append("\n")
            }
            reader.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return stringBuilder.toString()
    }

    // save HTML File
    @SuppressLint("SimpleDateFormat")
    fun saveHTMLFile(mActivity: Activity, providerName:String = "", saveInfoList:List<Info>) {
        val context: Context = mActivity.baseContext
        val resolver: ContentResolver = mActivity.contentResolver

        val htmlBuilder = StringBuilder()
        // header init
        val mHtmlHeader = readRawTextFile(context, R.raw.html_head)
        htmlBuilder.append(mHtmlHeader)

        saveInfoList.forEach { item ->
            htmlBuilder.append(
            """
                <tr>
                    <td>${item.name}</td>
                    <td>${item.value}</td>
                </tr>
            """.trimIndent()
            )
        }
        htmlBuilder.append("</table>")


        // 현재 날짜 형식 설정
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss") // 원하는 형식으로 변경 가능
        val currentDate = dateFormat.format(Date())

        val timeStampHtml =  """
                <div class="signature">
                    <p>Account Wallet Developer</p>
                    <p>Date : ${currentDate}</p>
                </div>
        """.trimIndent()

        val mHtmlTail:String = readRawTextFile(context, R.raw.html_tail)
        htmlBuilder.append(timeStampHtml)
        htmlBuilder.append(mHtmlTail)
        saveFile(context, providerName, htmlBuilder.toString(), ".html") // 저장 시작!
    }


    @SuppressLint("SimpleDateFormat")
    fun saveJsonFile(context: Context, providerName:String = "", saveInfoList:List<Info>) {

        val mInfoTitle = "UserInfo"
        val mJsonParent = JsonObject()
        // Device Info -> Json Data!
        try {
            val deviceObjList:MutableList<JsonObject> = mutableListOf()
            saveInfoList.forEach { info ->
                val jsonObj : JsonObject = info.toJsonObject()
                deviceObjList.add(jsonObj)
            }
            val deviceJsonArray: JsonArray = JsonParser.toJsonArray(deviceObjList)
            mJsonParent.add(mInfoTitle, deviceJsonArray)
        }catch (e:Exception){
            Logger.e("[msg] %s", e.message)
        }

        // 현재 날짜 형식 설정
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss") // 원하는 형식으로 변경 가능
        val currentDate = dateFormat.format(Date())

        // 날짜 데이터 추가
        mJsonParent.addProperty("Date", currentDate)
        val mJsonString:String = mJsonParent.toString()

        Logger.i("JSON Data : %s", mJsonString)

        // Json 파일로 저장
        saveFile(context, providerName, mJsonString, ".json")
    }


    fun saveFile(context: Context, providerName:String = "", content:String, format:String = ".txt"){
        val path:String = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).absolutePath
        val prefixName:String = if(providerName.isBlank() ) "None" else providerName
        val fileName = "user_info_${prefixName}$format"
        val file = File(path, fileName)
        Logger.i("[저장 경로] : %s", file.path)
        val preClearCmd = "rm -r ${file.path}"
        this.execute(preClearCmd)
        try {
            // 파일 쓰기
            FileOutputStream(file).use{ fileOutputStream ->
                OutputStreamWriter(fileOutputStream, StandardCharsets.UTF_8).use { outputStreamWriter ->
                    outputStreamWriter.write(content)
                }
            }
            val saveHeader = context.getString(R.string.txt_h_save_file)
            Toast.makeText(context, "$saveHeader $fileName", Toast.LENGTH_SHORT).show()
        } catch (e: IOException) {
            // e.printStackTrace()
            // 오류가 발생했을 때 오류 로그를 log.txt에 저장
            val errorLogFileName = "account_wallet_error_log.txt"
            val errorLogFile = File(path, errorLogFileName)
            try {
                FileOutputStream(errorLogFile).use { fileOutputStream ->
                    OutputStreamWriter(fileOutputStream, StandardCharsets.UTF_8).use { outputStreamWriter ->
                        outputStreamWriter.write("Error Log:\n${e.message}\n\n")
                        // Stack Trace:${Log.getStackTraceString(e)}
                    }
                }
                val failHeader = context.getString(R.string.txt_h_fail_save)
                Toast.makeText(context,
                    context.getString(R.string.txt_error_log_result, failHeader, errorLogFileName), Toast.LENGTH_SHORT).show()
            } catch (e: IOException) {
                // 오류 로그를 저장하는 도중에 또 다른 오류가 발생한 경우
                // e.printStackTrace()
                val failHeader = context.getString(R.string.txt_h_fail_save)
                Toast.makeText(context,
                    context.getString(R.string.txt_error_log_create, failHeader), Toast.LENGTH_SHORT).show()
            }
            val failHeader = context.getString(R.string.txt_h_fail_save)
            Toast.makeText(context, failHeader, Toast.LENGTH_SHORT).show()
        }
    }

}