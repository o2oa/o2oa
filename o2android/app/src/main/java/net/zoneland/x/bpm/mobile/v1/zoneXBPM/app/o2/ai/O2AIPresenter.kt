package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.o2.ai

import jiguang.chat.utils.pinyin.HanyuPinyin
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.O2
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.R
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BasePresenterImpl
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.TulingPostData
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.o2.TaskData
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XLog
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.o2Subscribe
import okhttp3.MediaType
import okhttp3.RequestBody
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.util.*

/**
 * Created by fancyLou on 15/05/2018.
 * Copyright © 2018 O2. All rights reserved.
 */


class O2AIPresenter : BasePresenterImpl<O2AIContract.View>(), O2AIContract.Presenter {


    //ai status
    private val STATUS_TOP = "top"  //第一层
    private val STATUS_TASK = "task" //任务工作层
    //ai route
    private val next_key_listen_command = "listencommand"
    private val next_key_finish = "finish"
    private val next_key_findtask = "findtask"
    private val next_key_ask_deal_next_task = "askdealnexttask"
    private val next_key_deal_task_with_neural = "dealtaskwithneural"


    private var status = STATUS_TOP
    private val currentTaskList = ArrayList<TaskData>()
    private var currentTaskIndex = 0




    override fun listenFinish(result: String) {
        XLog.info("listen  command is $result")
        when (status) {
            STATUS_TOP -> logicTop(result)
            STATUS_TASK -> logicTask(result)
        }
    }

    override fun listenError() {
        XLog.error("listen error.................")
        mView?.speak("对不起，我没有听清！", generateSpeakKey(next_key_listen_command))
    }

    override fun speakFinish(utteranceId: String?) {
        XLog.info("speak finish $utteranceId")
        if (utteranceId == null) {
            XLog.error("speak id 为空。。。。。。。。")
            return
        }

        if (utteranceId.startsWith(next_key_listen_command)) {
            mView?.beginListen()
        } else {
            when(status){
                STATUS_TOP->{
                    if (utteranceId.startsWith(next_key_finish)) {
                        mView?.finishAI()
                    }else if (utteranceId.startsWith(next_key_findtask)) {
                        findTask()
                    }
                }
                STATUS_TASK->{
                    if (utteranceId.startsWith(next_key_ask_deal_next_task)) {
                        askHowToDealWithNextTask()
                    }
                    if (utteranceId.startsWith(next_key_deal_task_with_neural)) {
                        dealWorkWithNeural()
                    }
                }
            }
        }
    }

    override fun speakError(utteranceId: String?) {
        XLog.error("speak error    $utteranceId")
        if (utteranceId == null) {
            XLog.error("speak id 为空。。。。。。。。")
            return
        }
        if (utteranceId.startsWith(next_key_listen_command)) {
            mView?.beginListen()
        } else {
            XLog.info("logic is not over , keep speaking.....")
        }
    }


    fun generateListenCommand() = generateSpeakKey(next_key_listen_command)

    fun reInitial() {
        status = STATUS_TOP
        currentTaskIndex = 0
        currentTaskList.clear()
    }



    private fun logicTop(result: String) {
        XLog.info("logic top ........")
        if (isInStopCommand(result)) {
            XLog.info("isInStopCommand.............")
            mView?.speak("感谢使用，下次再见！", generateSpeakKey(next_key_finish))
        } else if (isInTaskCommand(result)) {
            XLog.info("isInTaskCommand.............")
            //查询待办数据
            mView?.speak("正在查询您的工作", generateSpeakKey(next_key_findtask))
        } else {
//            mView?.speak("更多功能还在升级学习中，非常抱歉！", generateListenCommand())
            searchFromTuling123(result)
        }
    }


    private fun logicTask(result: String) {
        XLog.info("logic Task ...... $currentTaskIndex ${currentTaskList.size}")
        if (isInTaskNeuralCommand(result)){
            XLog.info("isInTaskNeuralCommand.............")
            mView?.speak("正在生成人工神经网络.提取您的处理数据进行分析.", generateSpeakKey(next_key_deal_task_with_neural))
        }else if (isInCurrentTaskRouteName(result)){
            XLog.info("isInCurrentTaskRouteName.............")
            dealWork(result)
        }else if (isInIgnoreCommand(result)) {//跳过，继续下一个
            XLog.info("isInIgnoreCommand.............")
            currentTaskIndex++
            mView?.speak("好的！", generateSpeakKey(next_key_ask_deal_next_task))
        }else if (isInStopCommand(result)){//退出
            XLog.info("isInStopCommand.............")
            status = STATUS_TOP
            mView?.speak("好的!还有什么需要我为您做的吗？", generateSpeakKey(next_key_listen_command))
        }else{
            mView?.speak("对不起，我无法处理这个命令。您可以选择 退出工作 或 继续下一个工作", generateSpeakKey(next_key_listen_command))
        }
    }




    private val TULING_API_KEY = "1bbde256119f4d6eaf3c25e67dedbd38"
    private fun searchFromTuling123(result: String) {
        val tulingService = getTuling123Service(mView?.getContext())
        if (tulingService==null) {
            mView?.speak("更多功能还在升级学习中，非常抱歉！", generateListenCommand())
        }else {
            tulingService.api(TulingPostData(TULING_API_KEY, result))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .o2Subscribe {
                        onNext { response->
                            when(response.code){
                                "100000" ->{
                                    mView?.speak(response.text, generateListenCommand())
                                }
                                "40001", "40002" ,"40004", "40007"->
                                {
                                    XLog.error("图灵识别错误，code:${response.code}, txt:${response.text}")
                                    mView?.speak("更多功能还在升级学习中，非常抱歉！", generateListenCommand())
                                }
                                else ->{
                                    XLog.info("图灵其他类型，code:${response.code}, txt:${response.text}")
                                    mView?.speak("更多功能还在升级学习中，非常抱歉！", generateListenCommand())
                                }
                            }
                        }
                    }
        }
    }


    private fun findTask() {
        val service = getProcessAssembleSurfaceServiceAPI(mView?.getContext())
        if (service == null) {
            mView?.speak("非常抱歉，查询工作异常，请稍后再试！", generateSpeakKey(next_key_listen_command))
        } else {
            service.getTaskListByPage(O2.FIRST_PAGE_TAG, O2.DEFAULT_PAGE_NUMBER).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .o2Subscribe {
                        onNext {
                            val list = it.data
                            if (list.isEmpty()) {
                                mView?.speak("没有查询到需要处理的工作！还有什么需要我为您做的吗？", generateSpeakKey(next_key_listen_command))
                            } else {
                                status = STATUS_TASK
                                currentTaskList.clear()
                                currentTaskList.addAll(list)
                                currentTaskIndex = 0
                                mView?.speak("您有${currentTaskList.size}项工作需要处理.", generateSpeakKey(next_key_ask_deal_next_task))
                            }
                        }
                        onError { e, isNetworkError ->
                            XLog.error("查询待办异常，$isNetworkError", e)
                            mView?.speak("没有查询到需要处理的工作！还有什么需要我为您做的吗？", generateSpeakKey(next_key_listen_command))
                        }
                    }
        }
    }



    private fun askHowToDealWithNextTask() {
        if (currentTaskIndex >= currentTaskList.size) {//任务处理完成，退回第一层
            status = STATUS_TOP
            mView?.speak("所有工作已经处理完成!还有什么需要我为您做的吗？", generateSpeakKey(next_key_listen_command))
        } else {
            val task = currentTaskList[currentTaskIndex]
            val person = task.creatorPerson.split("@")[0]
            val routeList = task.routeNameList.joinToString(separator = "或" )
            XLog.info(routeList)
            mView?.speak("来自${person}的${task.processName}，标题：${task.title}"+" , 您可以选择：$routeList", generateSpeakKey(next_key_listen_command))
        }
    }

    private fun dealWorkWithNeural() {
        val service = getProcessAssembleSurfaceServiceAPI(mView?.getContext())
        if (service==null){
            status = STATUS_TOP
            mView?.speak("工作处理服务异常，请稍后再试！", generateSpeakKey(next_key_listen_command))
        }else {
            val task = currentTaskList[currentTaskIndex]
            val taskBody = RequestBody.create(MediaType.parse("text/json"), "{\"type\":\"\"}")
            service.postTaskNeural(taskBody, task.id).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .o2Subscribe {
                        onNext { response->
                            val data = response.data
                            if (data!=null) {
                                val routename = data.routeName
                                var text = ""
                                val fromActivityName = ArrayList<String>()
                                val people = ArrayList<String>()
                                val worklogList = data.workLogList
                                worklogList.map { log->
                                    fromActivityName.add(log.fromActivityName)
                                    val tasklist = log.taskList
                                    if (tasklist!=null && tasklist.isNotEmpty()){
                                        tasklist.map {
                                            people.add(it.person.split("@")[0])
                                        }
                                    }
                                }
                                if (fromActivityName.isNotEmpty()){
                                    text += "工作已经到达 "+ fromActivityName.joinToString(separator = "和") + " 活动"
                                    if (people.isNotEmpty()) {
                                        text += "当前处理人："+people.joinToString(separator = "和")
                                    }
                                }else{
                                    text = "工作处理完成！"
                                }
                                currentTaskIndex++
                                mView?.speak("已经为您选择:$routename , $text", generateSpeakKey(next_key_ask_deal_next_task))
                            }else{
                                currentTaskIndex++
                                mView?.speak("工作处理完成！", generateSpeakKey(next_key_ask_deal_next_task))
                            }

                        }
                        onError { e, isNetworkError ->
                            XLog.error("神经网络处理任务失败，$isNetworkError", e)
                            val routeList = task.routeNameList.joinToString(separator = "或")
                            XLog.info(routeList)
                            mView?.speak("任务处理失败，当前任务无法自动判断，您可以选择：$routeList ", generateSpeakKey(next_key_listen_command))
                        }
                    }
        }
    }

    private fun dealWork(routeName: String) {
        val service = getProcessAssembleSurfaceServiceAPI(mView?.getContext())
        if (service==null){
            status = STATUS_TOP
            mView?.speak("工作处理服务异常，请稍后再试！", generateSpeakKey(next_key_listen_command))
        }else {
            val task = currentTaskList[currentTaskIndex]
            val taskBody = RequestBody.create(MediaType.parse("text/json"), "{\"routeName\":\"$routeName\", \"opinion\":\"\"}")
            service.postTask(taskBody, task.id).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .o2Subscribe {
                        onNext { response->
                            val data = response.data
                            if (data!=null && data.isNotEmpty()) {
                                var text = ""
                                val fromActivityName = ArrayList<String>()
                                val people = ArrayList<String>()
                                data.map {
                                    fromActivityName.add(it.fromActivityName)
                                    val tasklist = it.taskList
                                    if (tasklist!=null && tasklist.isNotEmpty()){
                                        tasklist.map {
                                            people.add(it.person.split("@")[0])
                                        }
                                    }
                                }
                                if (fromActivityName.isNotEmpty()){
                                    text += "工作已经到达 "+ fromActivityName.joinToString(separator = "和")+" 活动"
                                    if (people.isNotEmpty()) {
                                        text += "当前处理人："+people.joinToString(separator = "和")
                                    }
                                }else{
                                    text = "工作处理完成！"
                                }
                                currentTaskIndex++
                                mView?.speak(text, generateSpeakKey(next_key_ask_deal_next_task))
                            }else{
                                currentTaskIndex++
                                mView?.speak("工作处理完成！", generateSpeakKey(next_key_ask_deal_next_task))
                            }
                        }
                        onError { e, isNetworkError ->
                            XLog.error("任务处理失败，$isNetworkError", e)
                            val routeList = task.routeNameList.joinToString(separator = "或")
                            XLog.info(routeList)
                            mView?.speak("任务处理失败，您可以选择：$routeList ", generateSpeakKey(next_key_listen_command))
                        }
                    }
        }
    }


    private fun generateSpeakKey(key:String):String {
        return key + UUID.randomUUID().toString()
    }

    private fun isInCurrentTaskRouteName(result: String): Boolean {
        val task = currentTaskList[currentTaskIndex]
        val routeList = task.routeNameList
        val helper = HanyuPinyin()
        if (routeList.any { result.trim() == it }) {
            return true
        }else{
            val resultPiny = helper.getStringQuanPin(result)
            XLog.info("result:$result, pinyin:$resultPiny")
            return routeList.map {
                val pinyin = helper.getStringQuanPin(it)
                XLog.info("routName:$it ,pinyin:$pinyin")
                return@map pinyin
            }.any { resultPiny == it }
        }
    }
    private fun isInStopCommand(result: String): Boolean =
            (loadStopCommand()?.any { result.trim() == it } == true)

    private fun isInIgnoreCommand(result: String): Boolean =
            (loadIgnoreCommand()?.any { result.trim() == it } == true)

    private fun isInTaskCommand(result: String): Boolean =
            (loadTaskCommand()?.any { result.trim() == it } == true)

    private fun isInTaskNeuralCommand(result: String): Boolean =
            (loadTaskNeuralCommand()?.any { it == result.trim() } == true)

    private fun isInMusicCommand(result: String): Boolean =
            (loadMusicCommand()?.any { it == result.trim() } == true)


    private fun loadStopCommand(): Array<String>? =
            mView?.getContext()?.resources?.getStringArray(R.array.ai_command_stop)

    private fun loadIgnoreCommand(): Array<String>? =
            mView?.getContext()?.resources?.getStringArray(R.array.ai_command_ignore)

    private fun loadTaskCommand(): Array<String>? =
            mView?.getContext()?.resources?.getStringArray(R.array.ai_command_task)

    private fun loadTaskNeuralCommand(): Array<String>? =
            mView?.getContext()?.resources?.getStringArray(R.array.ai_command_task_neural)

    private fun loadMusicCommand(): Array<String>? =
            mView?.getContext()?.resources?.getStringArray(R.array.ai_command_music)

}