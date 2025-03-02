package com.aiden.accountwallet.util

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.widget.Toast
import com.aiden.accountwallet.BuildConfig
import com.aiden.accountwallet.R
import com.aiden.accountwallet.data.dto.Info
import com.aiden.accountwallet.data.model.IdAccountInfo
import com.aiden.accountwallet.data.model.IdProductKey
import com.aiden.accountwallet.data.vo.ImportProductKey
import com.aiden.accountwallet.data.vo.ImportUserAccount
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.nio.charset.StandardCharsets
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object FileManager {

    // 콜백 함수 추가된 버전
    interface FileListener {
        fun onFileSaved(savePath:String)
        fun onFileSaveListener(progress:Int)
        fun isStopSaveTask():Boolean
        fun onFileSaveFail()
    }

    // 콜백 함수 추가된 버전
    interface ReadListener {
        fun onFileRead(readData:String)
        fun onFileReadFail()
    }

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

    @SuppressLint("SimpleDateFormat")
    fun saveTextFile(
        context: Context, providerName:String = "", saveInfoList:List<Info>
    ){
        val sb = StringBuilder()
        saveInfoList.forEach { item ->
            sb.append("${item.name}\t\t: ${item.value}\n")
        }

        val mFileName = "${BuildConfig.APPLICATION_ID}_$providerName"
        saveFile(context, mFileName, sb.toString())
    }

    // save HTML File
    @SuppressLint("SimpleDateFormat")
    fun saveHTMLFile(
        mActivity: Activity,
        providerName:String = "",
        saveInfoList:List<Info>
    ) {
        val context: Context = mActivity.baseContext
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

        val mFileName = "${BuildConfig.APPLICATION_ID}_$providerName"
        saveFile(context, mFileName, htmlBuilder.toString(), ".html") // 저장 시작!
    }

    @SuppressLint("SimpleDateFormat")
    fun saveJsonFile(
        context: Context,
        providerName:String = "",
        saveInfoList:List<Info>
    ) {
        val mInfoTitle = "UserInfo"
        val mJsonParent = JsonObject()
        // Device Info -> Json Data!
        try {
            val mJsonData : JsonObject = AppJsonParser.getJsonObject(saveInfoList)
            mJsonParent.add(mInfoTitle, mJsonData)
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

        val mFileName = "${BuildConfig.APPLICATION_ID}_$providerName"
        // Json 파일로 저장
        saveFile(context, mFileName, mJsonString, ".json")
    }

    // * ----------------------------------------------------
    // *    Export Backup Data ...
    // * ----------------------------------------------------

    private fun saveFile(
        context: Context,
        mfileName:String = "",
        content:String,
        format:String = ".txt"
    ){
        val path:String = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).absolutePath
        val fileName = "$mfileName$format"
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

    private fun saveBackupFile(
        context: Context,
        mfileName:String = "",
        content:String,
        callBack:FileListener,
        currentProgress: Int
    ){
        CoroutineScope(Dispatchers.IO).launch {
            val path:String = Environment
                .getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS
                ).absolutePath
            val fileName = "$mfileName.json"
            val file = File(path, fileName)
            Logger.i("[저장 경로] : %s", file.path)
            val preClearCmd = "rm -r ${file.path}"
            this@FileManager.execute(preClearCmd)
            try {
                // 파일 쓰기
                val totalSize = content.toByteArray().size  // 전체 데이터 크기
                val bufferSize = 1024  // 1KB씩 저장
                var writtenSize = 0
                val progressUnit:Int = (100 - currentProgress)

                FileOutputStream(file).use { fos ->
                    content.toByteArray().inputStream().use { input ->
                        val buffer = ByteArray(bufferSize)
                        var bytesRead: Int

                        while (input.read(buffer).also { bytesRead = it } != -1) {
                            fos.write(buffer, 0, bytesRead)
                            writtenSize += bytesRead

                            // 진행률 계산 (소수점 제거)
                            val progress:Int = (writtenSize * progressUnit / totalSize)
                            callBack.onFileSaveListener(currentProgress + progress)

                            if(callBack.isStopSaveTask()){
                                throw IOException("User Request Stop Save Task...")
                            }
                        }
                    }
                }

                val mSavedPath = "/Download/$fileName"
                callBack.onFileSaved(mSavedPath) // 저장 경로 넘겨줌.

            } catch (e: IOException) {
                callBack.onFileSaveFail()
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
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context,
                            context.getString(R.string.txt_error_log_result,
                                failHeader, errorLogFileName), Toast.LENGTH_SHORT).show()
                    }

                } catch (e: IOException) {
                    // 오류 로그를 저장하는 도중에 또 다른 오류가 발생한 경우
                    // e.printStackTrace()
                    val failHeader = context.getString(R.string.txt_h_fail_save)
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context,
                            context.getString(R.string.txt_error_log_create,
                                failHeader), Toast.LENGTH_SHORT).show()
                    }
                }
                val failHeader:String = context.getString(R.string.txt_h_fail_save)
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, failHeader, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    fun exportJsonData(
        context: Context,
        saveAccountList:List<IdAccountInfo>,
        saveProductList:List<IdProductKey>,
        callBack: FileListener,
        currentProgress:Int = 0
    ) {

        val mAccountTag:String = context.getString(R.string.key_user_account)
        val mProductTag:String = context.getString(R.string.key_product)
        val mJsonParent = JsonObject()

        // 계정 정보 저장
        try {
            val deviceObjList:MutableList<JsonObject> = mutableListOf()
            saveAccountList.forEach { item ->
                val jsonObj : JsonObject = AppJsonParser.getJsonObject(context, item)
                deviceObjList.add(jsonObj)

            }
            val deviceJsonArray: JsonArray = AppJsonParser.toJsonArray(deviceObjList)
            mJsonParent.add(mAccountTag, deviceJsonArray)
        }catch (e:Exception){
            Logger.e("[msg] %s", e.message)
        }

        // 상품키 정보 저장
        try {
            val deviceObjList:MutableList<JsonObject> = mutableListOf()
            saveProductList.forEach { item ->
                val jsonObj : JsonObject = AppJsonParser.getJsonObject(context, item)
                deviceObjList.add(jsonObj)

            }
            val deviceJsonArray: JsonArray = AppJsonParser.toJsonArray(deviceObjList)
            mJsonParent.add(mProductTag, deviceJsonArray)
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

        val packageName = BuildConfig.APPLICATION_ID
        val lastSegment = packageName.substringAfterLast(".")
        val mFileName = "_backup_$lastSegment"
        // Json 파일로 저장
        saveBackupFile(context, mFileName, mJsonString, callBack, currentProgress)
    }


    // * ----------------------------------------------------
    // *    Import Backup Data ...
    // * ----------------------------------------------------

    fun readJsonFromUri(
        contentResolver: ContentResolver,
        uri: Uri
    ) : String {
        contentResolver.openInputStream(uri)?.use { inputStream ->
            val json:String = inputStream.bufferedReader().use { it.readText() }
            Logger.d("data : $json")
            return json
        }
        return ""
    }

}