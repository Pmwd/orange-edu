package com.orange.edu.media.service;


import com.orange.edu.media.model.po.MediaProcess;

import java.util.List;

/**
 * 媒资文件处理业务方法
 *
 * @author Wuxy
 * @version 1.0
 * @ClassName MediaFileProcessService
 * @since 2023/1/25 15:58
 */
public interface MediaFileProcessService {

    /**
     * 获取待处理任务
     *
     * @param shardIndex 分片序号
     * @param shardTotal 分片总数
     * @param count      获取数量
     * @return {@link List}<{@link com.orange.edu.media.model.po.MediaProcess}>
     */
    List<MediaProcess> getMediaProcessList(int shardIndex, int shardTotal, int count);


//    /**
//     * 将 url 存储至数据，并更新状态为成功，并将待处理视频记录删除存入历史
//     *
//     * @param status   处理结果，2:成功 3 失败
//     * @param fileId   文件 id
//     * @param url      文件访问 url
//     * @param errorMsg 失败原因
//     *
//     */
//    void saveProcessFinishStatus(String status, String fileId, String url, String errorMsg);

    /**
     * @description 保存任务结果
     * @param taskId  任务id
     * @param status 任务状态
     * @param fileId  文件id
     * @param url url
     * @param errorMsg 错误信息
     *
     */
    void saveProcessFinishStatus(Long taskId,String status,String fileId,String url,String errorMsg);

    /**
     *  开启一个任务
     * @param id 任务id
     * @return true开启任务成功，false开启任务失败
     */
    public boolean startTask(long id);

}
