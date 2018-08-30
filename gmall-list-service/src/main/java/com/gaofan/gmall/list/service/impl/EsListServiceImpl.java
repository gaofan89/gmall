package com.gaofan.gmall.list.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.gaofan.gmall.bean.SkuLsInfo;
import com.gaofan.gmall.bean.SkuLsParam;
import com.gaofan.gmall.service.EsListService;
import io.searchbox.client.JestClient;
import io.searchbox.client.JestResult;
import io.searchbox.core.Index;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.error.uri.ShouldHaveQuery;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.highlight.HighlightBuilder;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class EsListServiceImpl implements EsListService {

    @Autowired
    private JestClient jestClient;

    @Override
    public List<SkuLsInfo> search(SkuLsParam skuLsParam) {
        String searchStr = getElSearchStr(skuLsParam);
        Search search = new Search.Builder(searchStr).addIndex("gmall").addType("SkuLsInfo").build();

        List<SkuLsInfo> skuLsInfos = new ArrayList<>();

        try {
            JestResult jestResult = jestClient.execute(search);

            List<SearchResult.Hit<SkuLsInfo, Void>> hits = ((SearchResult) jestResult).getHits(SkuLsInfo.class);

            if(hits !=null && !hits.isEmpty()){

                for (SearchResult.Hit<SkuLsInfo, Void> hit : hits) {
                    SkuLsInfo skuLsInfo = hit.source;
                    skuLsInfos.add(skuLsInfo);
                }
            }


        } catch (IOException e) {
            e.printStackTrace();
        }


        return skuLsInfos;
    }

    private String getElSearchStr(SkuLsParam skuLsParam){

        SearchSourceBuilder dsl = new SearchSourceBuilder();

        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();

        if(StringUtils.isNoneBlank(skuLsParam.getCatalog3Id())){
            TermQueryBuilder termQueryBuilder = new TermQueryBuilder("catalog3Id",skuLsParam.getCatalog3Id());
            boolQueryBuilder.filter(termQueryBuilder);
        }

        String[] valueId = skuLsParam.getValueId();
        if(valueId !=null && valueId.length >0){
            for (String s : valueId) {
                TermQueryBuilder termQueryBuilder = new TermQueryBuilder("skuAttrValueList.valueId", s);
                boolQueryBuilder.filter(termQueryBuilder);
            }
        }

        if(StringUtils.isNotBlank(skuLsParam.getKeyword())){
            BoolQueryBuilder b2 = new BoolQueryBuilder();
            MatchQueryBuilder matchQueryBuilder = new MatchQueryBuilder("skuName",skuLsParam.getKeyword());
            b2.must(matchQueryBuilder);

            BoolQueryBuilder b3 = new BoolQueryBuilder();
            MatchQueryBuilder matchQueryBuilder1 = new MatchQueryBuilder("skuDesc",skuLsParam.getKeyword());
            b3.must(matchQueryBuilder1);

            boolQueryBuilder.should(b2).should(b3);
            boolQueryBuilder.minimumShouldMatch("1");

//            MatchQueryBuilder matchQueryBuilder = new MatchQueryBuilder("skuName",skuLsParam.getKeyword());
//            boolQueryBuilder.must(matchQueryBuilder);
//
//            MatchQueryBuilder matchQueryBuilder1 = new MatchQueryBuilder("skuDesc",skuLsParam.getKeyword());
//            boolQueryBuilder.must(matchQueryBuilder1);
        }

        dsl.query(boolQueryBuilder);
        dsl.size(skuLsParam.getPageSize());
        dsl.from((skuLsParam.getPageNo() -1) * skuLsParam.getPageSize());
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.field("skuName");
        highlightBuilder.preTags("<span style='color:red;font-weight:bolder;'>");
        highlightBuilder.postTags("</span>");
        dsl.highlight(highlightBuilder);
        System.out.println(dsl.toString());

        return dsl.toString();
    }


    private String getElSearchStrDemo(SkuLsParam skuLsParam){

        SearchSourceBuilder dsl = new SearchSourceBuilder();

        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();

        TermQueryBuilder termQueryBuilder = new TermQueryBuilder("catalog3Id","61");
        boolQueryBuilder.filter(termQueryBuilder);

        TermQueryBuilder termQueryBuilder1 = new TermQueryBuilder("skuAttrValueList.valueId", "51");
        boolQueryBuilder.filter(termQueryBuilder1);

        TermQueryBuilder termQueryBuilder2 = new TermQueryBuilder("skuAttrValueList.valueId", "54");
        boolQueryBuilder.filter(termQueryBuilder2);

        MatchQueryBuilder matchQueryBuilder = new MatchQueryBuilder("skuName","小米");
        boolQueryBuilder.must(matchQueryBuilder);

        MatchQueryBuilder matchQueryBuilder1 = new MatchQueryBuilder("skuDesc","小米");
        boolQueryBuilder.must(matchQueryBuilder1);

        dsl.query(boolQueryBuilder);
        dsl.size(100);
        dsl.from(0);


        System.out.println(dsl.toString());

        return dsl.toString();
    }
}
