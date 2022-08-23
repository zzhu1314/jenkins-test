package com.sentinel;

import com.alibaba.csp.sentinel.Entry;
import com.alibaba.csp.sentinel.SphU;
import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.slots.block.RuleConstant;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

/**
 * @Description
 * @Author zhuzhou
 * @Date 2022/8/19 17:06
 * @Version 1.0
 **/
@SpringBootApplication
@RestController
public class SentinelApplication {
    public static void main(String[] args) {
        SpringApplication springApplication = new SpringApplication();
        springApplication.run(SentinelApplication.class);
    }

    private final static  String RESOURCE_NAME1 = "helloWord1";
    private final static  String RESOURCE_NAME2 = "helloWord2";

    @GetMapping(value = "test")
    public String test(){
        try(Entry entry = SphU.entry(RESOURCE_NAME1)) {
              return UUID.randomUUID().toString();

        } catch (BlockException e) {
           return "被限流了";
        }catch (Throwable throwable){
            return "程序出错";
        }
    }


    @GetMapping(value = "/test2")
    @SentinelResource(value = RESOURCE_NAME2,blockHandler = "handler2",fallback = "fallback"
             )
    public String test2(){
        Random random = new Random();
        if(random.nextInt(10)<5){
            throw new RuntimeException();
        }
        return UUID.randomUUID().toString();
    }


    @GetMapping(value = "/test3")
    public String test3(){
        Random random = new Random();
        return UUID.randomUUID().toString();
    }

    public String handler2(BlockException e) {
        return "限流2222";
    }

    public String fallback(Throwable throwable){
        return "fallback";
    }

        @PostConstruct
    public void init(){
        List<FlowRule> flowRules = new ArrayList<>();
        FlowRule flowRule = new FlowRule();
        //设置资源名
        flowRule.setResource(RESOURCE_NAME1);
        //设置限流规则 根据qps限流
        flowRule.setGrade(RuleConstant.FLOW_GRADE_QPS);
        //设置限流总数
        flowRule.setCount(1);
        flowRules.add(flowRule);

        FlowRule flowRule1 = new FlowRule();
        //设置资源名
        flowRule1.setResource(RESOURCE_NAME2);
        //设置限流规则 根据qps限流
        flowRule1.setGrade(RuleConstant.FLOW_GRADE_QPS);
        //设置限流总数
        flowRule1.setCount(2);
        flowRules.add(flowRule1);
        flowRules.add(flowRule);
        FlowRuleManager.loadRules(flowRules);
    }
}
