package com.example.demo;

import com.alibaba.fastjson.JSON;
import com.example.demo.model.*;
import com.example.demo.repository.BlogUserMapper;
import com.example.demo.repository.IndexTestMapper;
import org.elasticsearch.action.admin.indices.get.GetIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.common.StopWatch;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.FetchSourceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
public class DemoApplicationTests {

    @Resource
    BlogUserMapper blogUserMapper;

    @Resource
    IndexTestMapper indexTestMapper;

    @Autowired
    private RestHighLevelClient client;

    private static String INDEX_TEST = null;
    private static EsTest tests = null;
    private static List<EsTest> testsList = null;

    @Test
    private void contextLoads() {
    }


    @Test
    public void testBlogUser() throws Exception {
        BlogUser user = new BlogUser();
        user.setId("11111111111");
        user.setUsername("admin");
        user.setPassword("1111111111");
        int i = blogUserMapper.insert(user);
        System.out.println(i);
    }


    /*新建索引*/
    @Test
    public void testIndex() throws IOException {

        initEsTest();

        INDEX_TEST = "index_test"; // 索引名称

//        // 判断是否存在索引
//        if (!existsIndex(INDEX_TEST)) {
//            // 不存在则创建索引
//            createIndex(INDEX_TEST);
//        }

        // 判断是否存在文档
//        if (!exists(INDEX_TEST, tests)) {
//            // 不存在增加文档
//            add(INDEX_TEST, tests);
//        }

        //获取单条文档信息
//        get(INDEX_TEST,tests.getId());

        //更新单条文档信息
//        update(INDEX_TEST,tests);

        //搜索
//        search(INDEX_TEST,tests.getName());

        //删除单条文档
//        delete(INDEX_TEST,tests.getId());

        //bulk批量操作
//        bulk();

        //模糊检索
        searchShould(INDEX_TEST,"this");

    }

    /*mysql插入100条数据*/
    @Test
    public void mysqlTest(){
        IndexTest indexTest = null;
        for (int i = 0; i < 1000; i++) {
            indexTest = new IndexTest();
            indexTest.setId(Long.valueOf(i));
            indexTest.setName("this is the test " + i);
            indexTestMapper.insert(indexTest);
        }
    }

    /*mysql全文检索*/
    @Test
    public void mysqlSearch(){
        StopWatch watch = new StopWatch();
        watch.start();

        IndexTestExample example = new IndexTestExample();
        IndexTestExample.Criteria criteria = example.createCriteria();
        criteria.andNameLike("this");
        List<IndexTest> indexTests = indexTestMapper.selectByExample(example);

        watch.stop();
        TimeValue timeValue = watch.totalTime();
        double millisFrac = timeValue.getMillisFrac();
        double secondsFrac = timeValue.getSecondsFrac();
        System.out.println("毫秒：" + millisFrac + "  " + "秒：" + secondsFrac);
    }

    private void initEsTest() {
        testsList = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            tests = new EsTest();
            tests.setId(Long.valueOf(i));
            tests.setName("this is the test " + i);
            testsList.add(tests);
        }
    }

    /**
     * es 模糊检索
     */
    public void searchShould(String index, String content) throws IOException {
        StopWatch watch = new StopWatch();
        watch.start();

        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.should(QueryBuilders.matchPhraseQuery("name",content));
        
        String s = boolQueryBuilder.toString();
//        System.out.println("构建的es表达式："+s);

        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.query(boolQueryBuilder);
        sourceBuilder.from(0);
        sourceBuilder.size(100);
        sourceBuilder.fetchSource(new String[] { "name" }, new String[] {});
        SearchRequest searchRequest = new SearchRequest(index);
        searchRequest.source(sourceBuilder);
        SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);
//        System.out.println("search: " + JSON.toJSONString(response));

        watch.stop();
        TimeValue timeValue = watch.totalTime();
        double millisFrac = timeValue.getMillisFrac();
        double secondsFrac = timeValue.getSecondsFrac();
        System.out.println("毫秒：" + millisFrac + "  " + "秒：" + secondsFrac);

//        SearchHits hits = response.getHits();
//        SearchHit[] searchHits = hits.getHits();
//        for (SearchHit hit : searchHits) {
//            System.out.println("search -> " + hit.getSourceAsString() + "  " + hit.getId());
//        }
    }

    /**
     * 搜索
     * @param index 索引
     * @param name 搜索的内容
     * @throws IOException
     */
    public void search(String index, String name) throws IOException {
        BoolQueryBuilder boolBuilder = QueryBuilders.boolQuery();
        boolBuilder.must(QueryBuilders.matchQuery("name", name)); // 这里可以根据字段进行搜索，must表示符合条件的，相反的mustnot表示不符合条件的
        // boolBuilder.must(QueryBuilders.matchQuery("id", tests.getId().toString()));
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.query(boolBuilder);
        sourceBuilder.from(0);
        sourceBuilder.size(100); // 获取记录数，默认10
        sourceBuilder.fetchSource(new String[] { "id", "name" }, new String[] {}); // 第一个是获取字段，第二个是过滤的字段，默认获取全部
        SearchRequest searchRequest = new SearchRequest(index);
        searchRequest.source(sourceBuilder);
        SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);
        System.out.println("search: " + JSON.toJSONString(response));
        SearchHits hits = response.getHits();
        SearchHit[] searchHits = hits.getHits();
        for (SearchHit hit : searchHits) {
            System.out.println("search -> " + hit.getSourceAsString());
        }
    }

    /**
     * 批量操作
     * @throws IOException
     */
    public void bulk() throws IOException {
        // 批量增加
        BulkRequest bulkAddRequest = new BulkRequest();
        for (int i = 0; i < testsList.size(); i++) {
            tests = testsList.get(i);
            IndexRequest indexRequest = new IndexRequest(INDEX_TEST).id(tests.getId().toString());
            indexRequest.source(JSON.toJSONString(tests), XContentType.JSON);
            bulkAddRequest.add(indexRequest);
        }
        BulkResponse bulkAddResponse = client.bulk(bulkAddRequest, RequestOptions.DEFAULT);
//        System.out.println("bulkAdd: " + JSON.toJSONString(bulkAddResponse));
//        search(INDEX_TEST, "this");

//        // 批量更新
//        BulkRequest bulkUpdateRequest = new BulkRequest();
//        for (int i = 0; i < testsList.size(); i++) {
//            tests = testsList.get(i);
//            tests.setName(tests.getName() + " updated");
//            UpdateRequest updateRequest = new UpdateRequest(INDEX_TEST, tests.getId().toString());
//            updateRequest.doc(JSON.toJSONString(tests), XContentType.JSON);
//            bulkUpdateRequest.add(updateRequest);
//        }
//        BulkResponse bulkUpdateResponse = client.bulk(bulkUpdateRequest, RequestOptions.DEFAULT);
//        System.out.println("bulkUpdate: " + JSON.toJSONString(bulkUpdateResponse));
//        search(INDEX_TEST, "updated");
//
        // 批量删除
//        BulkRequest bulkDeleteRequest = new BulkRequest();
//        for (int i = 0; i < testsList.size(); i++) {
//            tests = testsList.get(i);
//            DeleteRequest deleteRequest = new DeleteRequest(INDEX_TEST, tests.getId().toString());
//            bulkDeleteRequest.add(deleteRequest);
//        }
//        BulkResponse bulkDeleteResponse = client.bulk(bulkDeleteRequest, RequestOptions.DEFAULT);
//        System.out.println("bulkDelete: " + JSON.toJSONString(bulkDeleteResponse));
//        search(INDEX_TEST, "this");
    }

    /**
     * 获取记录信息
     * @param index 索引
     * @param id 文档id
     * @throws IOException
     */
    public void get(String index, Long id) throws IOException {
        GetRequest getRequest = new GetRequest(index, id.toString());
        GetResponse getResponse = client.get(getRequest, RequestOptions.DEFAULT);
        System.out.println("get: " + JSON.toJSONString(getResponse));
    }

    /**
     * 更新记录信息
     * @param index 索引
     * @param tests 对象
     * @throws IOException
     */
    public void update(String index, EsTest tests) throws IOException {
        tests.setName(tests.getName() + "updated");
        UpdateRequest request = new UpdateRequest(index, tests.getId().toString());
        request.doc(JSON.toJSONString(tests), XContentType.JSON);
        UpdateResponse updateResponse = client.update(request, RequestOptions.DEFAULT);
        System.out.println("update: " + JSON.toJSONString(updateResponse));
    }

    /**
     * 删除记录
     * @param index 索引
     * @param id 文档id
     * @throws IOException
     */
    public void delete(String index, Long id) throws IOException {
        DeleteRequest deleteRequest = new DeleteRequest(index, id.toString());
        DeleteResponse response = client.delete(deleteRequest, RequestOptions.DEFAULT);
        System.out.println("delete: " + JSON.toJSONString(response));
    }

    /**
     * 增加记录
     * @param index 索引
     * @param tests 对象
     * @throws IOException
     */
    public void add(String index, EsTest tests) throws IOException {
        IndexRequest indexRequest = new IndexRequest(index).id(tests.getId().toString());
        indexRequest.source(JSON.toJSONString(tests), XContentType.JSON);
        IndexResponse indexResponse = client.index(indexRequest, RequestOptions.DEFAULT);
        System.out.println("add: " + JSON.toJSONString(indexResponse));
    }

    /**
     * 判断记录是都存在
     * @param index 索引
     * @param tests 对象
     * @return
     * @throws IOException
     */
    public boolean exists(String index, EsTest tests) throws IOException {
        GetRequest getRequest = new GetRequest(index, tests.getId().toString());
        getRequest.fetchSourceContext(new FetchSourceContext(false));
        getRequest.storedFields("_none_");
        boolean exists = client.exists(getRequest, RequestOptions.DEFAULT);
        System.out.println("exists: " + exists);
        return exists;
    }

    /**
     * 创建索引
     * @param index 索引
     * @throws IOException
     */
    private void createIndex(String index) throws IOException {
        CreateIndexRequest request = new CreateIndexRequest(index);
        CreateIndexResponse createIndexResponse = client.indices().create(request, RequestOptions.DEFAULT);
        System.out.println("createIndex: " + JSON.toJSONString(createIndexResponse));
    }


    /**
     * 判断索引是否存在
     * @param index 索引
     * @throws IOException
     */
    private boolean existsIndex(String index) throws IOException {
        GetIndexRequest request = new GetIndexRequest();
        request.indices(index);
        boolean exists = client.indices().exists(request, RequestOptions.DEFAULT);
        System.out.println("existsIndex: " + exists);
        return exists;
    }
}