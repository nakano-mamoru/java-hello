package com.example.demo;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

@SpringBootTest
class DemoApplicationTest {

    @Autowired
    private DemoApplication demoApplication;

    @Test
    void contextLoads() {
        // これで main クラスの @SpringBootApplication 部分はカバー
    }

    // @Test
    // void exampleMethod_normal() {
    //     demoApplication.exampleMethod("hello");
    //     // 標準出力のテストは省略、呼べればOK
    // }

    // @Test
    // void exampleMethod_null_throws() {
    //     assertThrows(IllegalArgumentException.class, () -> demoApplication.exampleMethod(null));
    // }

    @Test
    void mainMethod_runs() {
        // main メソッドも呼ぶことでカバレッジ反映
        DemoApplication.main(new String[]{});
    }
}