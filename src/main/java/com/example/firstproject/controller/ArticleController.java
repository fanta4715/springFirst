package com.example.firstproject.controller;

import com.example.firstproject.dto.ArticleForm;
import com.example.firstproject.entity.Article;
import com.example.firstproject.repository.ArticleRepository;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;

@Slf4j // Simple Logging Facade for Java 로깅기능 사용가능
@Controller
public class ArticleController {
    @Autowired
    private ArticleRepository articleRepository;
    @GetMapping("/articles/new")
    public String newArticleForm(){
        return "articles/new";
    }
    @PostMapping("/articles/create")
    public String createArticle(ArticleForm form){
        log.info(form.toString());
//        System.out.println(form.toString());
        //1. DTO를 엔티티로 변환
        Article article = form.toEntity();
        log.info(article.toString());
        //        System.out.println(article.toString());
        //2. 리포지토리를 활용해 엔티티를 DB에 저장
        Article saved = articleRepository.save(article);
        log.info(saved.toString());
        //        System.out.println(saved.toString());

        return "redirect:/articles/"+saved.getId();
    }

    @GetMapping("/articles/{id}")
    public String show(@PathVariable Long id, Model model){
        log.info("id="+id);
        //1. id에 해당하는 DB 가져오기 (by repository)

        //데이터가 없으면 null저장
        //findById 로 찾는 반환형은 Optinal<Article>임

        Article articleEntity=articleRepository.findById(id).orElse(null);

        //2. 가져온 데이터 모델에 등록하기
//        model.addAttribute(String name,Object value);
        model.addAttribute("article",articleEntity);

        //3. 뷰 페이지 만들고 반환하기
        //url을 건드리는건 아니네!
        return "articles/show";
    }
    @GetMapping("/articles")
    public String index(Model model){
        //1. DB에서 모든 ARticle 가져오기
        //findAll() 반환타입 Iterable
        // Iterable -> Collection -> List 순
        // 타입 불일치 해결 방법
        //   1) 다운캐스팅
        //   2) 오버라이딩 (레포지토리에서 함수 반환형 재선언)
        //   3) Iterable로 받음
        ArrayList<Article> articleList= articleRepository.findAll();

        //2. 가져온 Article 묶음 모델에 등록
        model.addAttribute("articleList",articleList);

        //3. 뷰 페이지 설정
        return "articles/index";


        //return "";
    }

    @GetMapping("/articles/{id}/edit")
    public String edit(@PathVariable Long id,Model model){

        //뷰 페이지 설정하기
        Article articleEntity =articleRepository.findById(id).orElse(null);
        model.addAttribute("article",articleEntity);
        return "articles/edit";
    }

    @PostMapping("/articles/update")
    public String update(ArticleForm form){
        log.info(form.toString());
        //1. DTO를 엔티티로 변환하기
        Article articleEntity=form.toEntity();
        //2. 엔티티를 DB에 저장
        //  기존의 엔티티를 수정해서 넣어야함
        //2-1. DB에서 기존 데이터 가져오기
        Article target= articleRepository.findById(articleEntity.getId()).orElse(null);
        //2-2. 기존데이터값 갱신
        if (target!=null){
            articleRepository.save(articleEntity);
        }
        //이거는 그냥 articles/update의 주소인데 아무것도 안 띄우는 걸 의미함


        //3. 수정 결과 페이지로 리다이렉트
       return "redirect:/articles/"+articleEntity.getId();


    }

    @GetMapping("articles/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes rttr){
        log.info("삭제 요청이 들어왔습니다!");
//        1. 삭제할 대상 가져옴
        Article target=articleRepository.findById(id).orElse(null);
        log.info(target.toString());

//        2. 대상 엔티티 삭제
        if (target!=null){
            articleRepository.delete(target);
//            얘가 이제 msg라는 값을 가짐
//            모델의 attribute와 비슷한데, 리다이렉트용
            rttr.addFlashAttribute("msg","삭제됐습니다");
        }
//        3. 결과 페이지 리다이렉
        //이 리턴은 "밑의 주소로 가주세요~"가 아니다.
        //밑의 주소로 static이랑 template를 뒤져주세요다
        //그렇기때문에, 주소를 원한다면, redirect를 해라
        return "redirect:/articles";
    }
}
