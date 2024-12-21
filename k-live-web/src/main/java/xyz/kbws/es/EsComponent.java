package xyz.kbws.es;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.script.Script;
import org.elasticsearch.script.ScriptType;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.elasticsearch.xcontent.XContentType;
import org.springframework.stereotype.Component;
import xyz.kbws.common.ErrorCode;
import xyz.kbws.config.AppConfig;
import xyz.kbws.exception.BusinessException;
import xyz.kbws.model.entity.User;
import xyz.kbws.model.entity.Video;
import xyz.kbws.model.enums.SearchOrderTypeEnum;
import xyz.kbws.model.esDto.VideoEsDto;
import xyz.kbws.service.UserService;

import javax.annotation.Resource;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author kbws
 * @date 2024/12/14
 * @description: ES 组件
 */
@Slf4j
@Component
public class EsComponent {

    @Resource
    private AppConfig appConfig;

    @Resource
    private RestHighLevelClient restHighLevelClient;

    @Resource
    private UserService userService;


    private Boolean isExistIndex() throws IOException {
        GetIndexRequest getIndexRequest = new GetIndexRequest(appConfig.getEsIndexName());
        return restHighLevelClient.indices().exists(getIndexRequest, RequestOptions.DEFAULT);
    }

    public void createIndex() {
        try {
            if (isExistIndex()) {
                return;
            }
            CreateIndexRequest createIndexRequest = new CreateIndexRequest(appConfig.getEsIndexName());
            createIndexRequest.settings("{\"analysis\": {\n" +
                    "    \"analyzer\": {\n" +
                    "        \"comma\": {\n" +
                    "            \"type\": \"pattern\",\n" +
                    "              \"pattern\": \",\"\n" +
                    "            }\n" +
                    "        }\n" +
                    "    }}", XContentType.JSON);

            createIndexRequest.mapping("{\n" +
                    "    \"properties\": {\n" +
                    "        \"id\": {\n" +
                    "            \"type\": \"text\",\n" +
                    "            \"index\": false\n" +
                    "        },\n" +
                    "        \"userId\": {\n" +
                    "            \"type\": \"text\",\n" +
                    "            \"index\": false\n" +
                    "        },\n" +
                    "        \"cover\": {\n" +
                    "            \"type\": \"text\",\n" +
                    "            \"index\": false\n" +
                    "        },\n" +
                    "        \"name\": {\n" +
                    "            \"type\": \"text\",\n" +
                    "            \"analyzer\": \"ik_max_word\"\n" +
                    "        },\n" +
                    "        \"tags\": {\n" +
                    "            \"type\": \"text\",\n" +
                    "            \"analyzer\": \"comma\"\n" +
                    "        },\n" +
                    "        \"playCount\": {\n" +
                    "            \"type\": \"integer\",\n" +
                    "            \"index\": false\n" +
                    "        },\n" +
                    "        \"danmuCount\": {\n" +
                    "            \"type\": \"integer\",\n" +
                    "            \"index\": false\n" +
                    "        },\n" +
                    "        \"collectCount\": {\n" +
                    "            \"type\": \"integer\",\n" +
                    "            \"index\": false\n" +
                    "        },\n" +
                    "        \"createTime\": {\n" +
                    "            \"type\": \"date\",\n" +
                    "            \"format\": \"yyyy-MM-dd HH:mm:ss\",\n" +
                    "            \"index\": false\n" +
                    "        }\n" +
                    "    }\n" +
                    "}", XContentType.JSON);
            CreateIndexResponse createIndexResponse = restHighLevelClient.indices().create(createIndexRequest, RequestOptions.DEFAULT);
            boolean acknowledged = createIndexResponse.isAcknowledged();
            if (!acknowledged) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "初始化 ES 失败");
            }
        } catch (Exception e) {
            log.error("初始化 ES 失败: {}", e.getMessage());
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "初始化 ES 失败");
        }
    }

    public void saveDoc(Video video) {
        try {
            if (docExist(video.getId())) {
                updateDoc(video);
            } else {
                VideoEsDto videoEsDto = new VideoEsDto();
                BeanUtil.copyProperties(video, videoEsDto);
                videoEsDto.setCollectCount(0);
                videoEsDto.setPlayCount(0);
                videoEsDto.setDanmuCount(0);
                IndexRequest indexRequest = new IndexRequest(appConfig.getEsIndexName());
                indexRequest.id(video.getId()).source(JSONUtil.toJsonStr(videoEsDto), XContentType.JSON);
                restHighLevelClient.index(indexRequest, RequestOptions.DEFAULT);
            }
        } catch (Exception e) {
            log.error("保存到 ES 失败: {}", e.getMessage());
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "保存到 ES 失败");
        }
    }

    private void updateDoc(Video video) {
        try {
            video.setCreateTime(null);
            video.setLastPlayTime(null);
            Map<String, Object> dataMap = new HashMap<>();
            Field[] fields = video.getClass().getDeclaredFields();
            for (Field field : fields) {
                String methodName = "get" + StrUtil.upperFirst(field.getName());
                Method method = video.getClass().getMethod(methodName);
                Object object = method.invoke(video);
                if (object != null && object instanceof String && !StrUtil.isEmpty(object.toString()) || object != null && !(object instanceof String)) {
                    dataMap.put(field.getName(), object);
                }
            }
            if (dataMap.isEmpty()) {
                return;
            }
            UpdateRequest updateRequest = new UpdateRequest(appConfig.getEsIndexName(), video.getId());
            updateRequest.doc(dataMap);
            restHighLevelClient.update(updateRequest, RequestOptions.DEFAULT);
        } catch (Exception e) {
            log.error("ES 更新视频失败: {}", e.getMessage());
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "ES 更新视频失败");
        }
    }

    public void updateDocCount(String videoId, String fieldName, Integer count) {
        try {
            UpdateRequest updateRequest = new UpdateRequest(appConfig.getEsIndexName(), videoId);
            Script script = new Script(ScriptType.INLINE, "painless", "ctx._source." + fieldName + " += params.count", Collections.singletonMap("count", count));
            updateRequest.script(script);
            restHighLevelClient.update(updateRequest, RequestOptions.DEFAULT);
        } catch (Exception e) {
            log.error("更新 ES 视频失败: {}", e.getMessage());
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "保存到 ES 失败");
        }
    }

    public void deleteDoc(String videoId) {
        try {
            DeleteRequest deleteRequest = new DeleteRequest(appConfig.getEsIndexName(), videoId);
            restHighLevelClient.delete(deleteRequest, RequestOptions.DEFAULT);
        } catch (Exception e) {
            log.error("ES 删除视频失败: {}", e.getMessage());
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "ES 删除视频失败");
        }
    }

    public Page<Video> search(Boolean isHighlight, String keyWord, Integer orderType, Integer current, Integer pageSize) {
        try {
            SearchOrderTypeEnum orderTypeEnum = SearchOrderTypeEnum.getEnumByValue(orderType);

            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
            searchSourceBuilder.query(QueryBuilders.multiMatchQuery(keyWord, "name", "tags"));

            // 高亮
            if (isHighlight) {
                HighlightBuilder highlightBuilder = new HighlightBuilder();
                highlightBuilder.field("name");
                highlightBuilder.preTags("<span class='highlight'>");
                highlightBuilder.postTags("</span>");
                searchSourceBuilder.highlighter(highlightBuilder);
            }

            // 排序
            searchSourceBuilder.sort("_score", SortOrder.ASC);
            if (orderType != null) {
                searchSourceBuilder.sort(orderTypeEnum.getField(), SortOrder.ASC);
            }
            current = current == null ? 1 : current;
            pageSize = pageSize == null ? 20 : pageSize;
            searchSourceBuilder.size(pageSize);
            searchSourceBuilder.from((current - 1) * pageSize);
            SearchRequest searchRequest = new SearchRequest(appConfig.getEsIndexName());
            searchRequest.source(searchSourceBuilder);
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            SearchHits searchHits = searchResponse.getHits();
            Integer totalCount = (int) searchHits.getTotalHits().value;
            List<Video> videoList = new ArrayList<>();
            List<String> userIdList = new ArrayList<>();
            for (SearchHit searchHit : searchHits.getHits()) {
                Video video = JSONUtil.toBean(searchHit.getSourceAsString(), Video.class);
                if (searchHit.getHighlightFields().get("name") != null) {
                    video.setName(searchHit.getHighlightFields().get("name").fragments()[0].toString());
                }
                videoList.add(video);
                userIdList.add(video.getUserId());
            }
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            queryWrapper.in("id", userIdList);
            List<User> userList = userService.list(queryWrapper);
            Map<String, User> userMap = userList.stream()
                    .collect(Collectors.toMap(
                            User::getId,
                            Function.identity(),
                            (data1, data2) -> data2
                    ));
            videoList.forEach(item -> {
                item.setNickName(userMap.get(item.getUserId()).getNickName());
            });
            Page<Video> page = new Page<>();
            page.setTotal(totalCount);
            page.setCurrent(current);
            page.setSize(pageSize);
            page.setRecords(videoList);
            return page;
        } catch (Exception e) {
            log.error("ES 查询视频失败: {}", e.getMessage());
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "ES 查询视频失败");
        }
    }

    private Boolean docExist(String id) throws IOException {
        GetRequest getRequest = new GetRequest(appConfig.getEsIndexName(), id);
        GetResponse getResponse = restHighLevelClient.get(getRequest, RequestOptions.DEFAULT);
        return getResponse.isExists();
    }
}
