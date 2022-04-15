package multi.fclass.iMint.security.controller;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.security.Principal;
import java.util.Collection;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.WebAttributes;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import multi.fclass.iMint.security.auth.provider.KakaoUserInfo;
import multi.fclass.iMint.security.auth.provider.NaverUserInfo;
import multi.fclass.iMint.security.dao.IUserDAO;
import multi.fclass.iMint.security.dto.Role;
import multi.fclass.iMint.security.dto.User;
import multi.fclass.iMint.security.parsing.mbid.ParseMbId;
import multi.fclass.iMint.security.parsing.role.ParseMbRole;

@Slf4j // 로그
@Controller // 뷰 반환
public class IndexController {

	@Autowired
	private IUserDAO userdao;
	
	@Autowired
	private User user;
	
	@Autowired
	ParseMbRole parseMbRole;
	
	@Autowired
	ParseMbId parseMbId;
	
	// 로그인 
	@GetMapping({"", "/"})
	public String loginForm() {
		return "member/login";
	}
	
	// 가입 완료시키기
	@RequestMapping(value = "/err/denied-page") // 안온다 
	public ModelAndView accessDenied(Authentication auth, HttpServletRequest req){
		
		ModelAndView mv = new ModelAndView();
		
		AccessDeniedException ade = (AccessDeniedException) req.getAttribute(WebAttributes.ACCESS_DENIED_403);
//        log.info("---------- Log 테스트 ---------");
//		log.info("auth : {}", auth.getPrincipal()); // 로그 기록
//		log.info("exception : {}", ade); // 로그 기록
        
		// 모듈화 
//		DefaultOAuth2User authorization = (DefaultOAuth2User) auth.getPrincipal();
//        System.out.println(authorization);
//
//		Collection<? extends GrantedAuthority> auth2 = authorization.getAuthorities();
//        System.out.println(auth2.toString());
		
		// 모듈화 결과(아래 1줄)
		String auth2 = parseMbRole.parseMbRole(auth);
		
        //// 끝
        
		mv.addObject("auth", auth2);
		mv.addObject("errMsg", ade);
		
		mv.setViewName("err/deniedpage");
		
		return mv;
	}

	// 회원가입은 총 4단계 
	// 회원가입 2(보호자, 아이 모두): sns 가입(회원가입 1)후 register 페이지로 이동
	@GetMapping("/register")
	public ModelAndView registersns(Authentication auth) { // Authentication auth -> mbId로 연결하기 & 수정 & 권한 업데이트

		// 모듈화 결과(아래 2줄)
		String mbId = parseMbId.parseMbId(auth);
		User user = parseMbId.getUserMbId(mbId);
//		Role mbRole = parseMbId.getRoleMbId(mbId);
		
		ModelAndView mv = new ModelAndView();

		mv.addObject("user", user);
		mv.setViewName("member/register");
		return mv;
	}
	
	// 회원가입 3(보호자): 내 동네 설정 -> 보호자, 관리자 권한 부여
//	@PreAuthorize("hasRole('ROLE_uncerti_GAURD') or hasRole('ROLE_GAURD') or hasRole('ROLE_ADMIN')")

	@ResponseBody
	@RequestMapping(value = "/kakao/local", method = RequestMethod.GET, produces = {"application/json;charset=utf-8"})
	public String gaurdlocation1(String latitude, String longitude, String mbId, String mbNick, Role mbRole, String mbEmail, String mbInterest) { // 
		String GEOCODE_URL="http://dapi.kakao.com/v2/local/search/address.json?"; 
		String GEOCODE_USER_INFO="KakaoAK 81c7bda99c1d17edaf364c7a1fe1b80d"; 
			URL obj; 
			try{
				obj = new URL(GEOCODE_URL+"x="+latitude+"&y="+longitude); 
				HttpURLConnection con = (HttpURLConnection)obj.openConnection(); 
				//get으로 받아오면 된다. 자세한 사항은 카카오개발자센터에 나와있다. 
				con.setRequestMethod("GET"); 
				con.setRequestProperty("Authorization",GEOCODE_USER_INFO); 
				con.setRequestProperty("content-type", "application/json"); 
				con.setDoOutput(true); 
				con.setUseCaches(false); 
				con.setDefaultUseCaches(false); 
				Charset charset = Charset.forName("UTF-8"); 
				BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream(), charset)); 
				String inputLine; 
				StringBuffer response = new StringBuffer(); 
				while ((inputLine = in.readLine()) != null) { 
					response.append(inputLine); 
					} //response 객체를 출력해보자 
				System.out.println(response.toString());
			} 
				catch (Exception e) { 
					e.printStackTrace(); 
			}
			return "{response:response}";
	}

//		
//		ModelAndView mv = new ModelAndView();
//		User user = parseMbId.getUserMbId(mbId);
//		user.setMbNick(mbNick);
//		user.setMbRole(mbRole);
//		user.setMbEmail(mbEmail);
//		user.setMbInterest(mbInterest);
//		mv.addObject("user", user); 
//		mv.setViewName("member/guard-mypage/guard-location");
//		return mv;

	@ResponseBody
//	@RequestMapping(value = "/mypage/location", method = RequestMethod.GET, produces = {"application/json;charset=utf-8"})
	@RequestMapping("/mypage/location")
	public ModelAndView gaurdlocation(String mbId, String mbNick, Role mbRole, String mbEmail, String mbInterest, String mbLocation) { // String mbId, String mbNick, Role mbRole, String mbEmail, String mbInterest
		
		ModelAndView mv = new ModelAndView();
		
		User user = parseMbId.getUserMbId(mbId);
		System.out.println(mbId);
		System.out.println(mbNick);
		System.out.println(mbRole);

		//		user.setMbNick(mbNick);
//		user.setMbRole(mbRole);
//		user.setMbEmail(mbEmail);
//		user.setMbInterest(mbInterest);
//		user.setMbLocation(mbLocation);
//		userdao.savedetails(mbId, mbNick, mbRole, mbEmail, mbInterest, mbLocation, null); // 1차 저장 
		mv.addObject("user", user);  // 객체 추가할 때 user 객체 
		mv.setViewName("member/guard-mypage/guard-location");
		return mv;
	}
	
	// 회원가입 3(아이): 보호자 연결 설정 -> 아이, 관리자 권한 부여
	//@PreAuthorize("hasRole('ROLE_uncerti_CHILD') or hasRole('ROLE_CHILD') or hasRole('ROLE_ADMIN')")
	@GetMapping("/register/connect")		
	public ModelAndView babyconnect(String mbId, String mbNick, Role mbRole, String mbEmail, String mbInterest) {
		ModelAndView mv = new ModelAndView();
		User user = userdao.findByMbId(mbId);
		user.setMbId(mbId);
		user.setMbNick(mbNick);
		user.setMbRole(mbRole);
		user.setMbEmail(mbEmail);
		user.setMbInterest(mbInterest);
		
		mv.addObject("user", user);  // 객체 추가할 때 user 객체 
		mv.setViewName("member/register_connect");
		return mv;
	}

	
	// 회원가입 4(최종. 보호자, 아이 모두)
	// 회원가입 마치면 부모-> 위치 설정 , 아이 -> 보호자 연동 후 권한을 인증으로 변경
	@PostMapping("/register")
	public String registerdetails(Role mbRole, String mbId, String mbNick, String mbEmail, String mbInterest, String mbLocation, String mbGuard) {
			
	if (mbRole == Role.UN_GAURD) { // 보호자 
		mbGuard = null;
		mbRole = Role.GAURD;
	}	
	else if (mbRole == Role.UN_CHILD) { // 아이 
		mbLocation = null;
		mbRole = Role.CHILD;		
	}	

	// 유저 싱글톤? 나중에 고려
	userdao.savedetails(mbId,mbNick,mbRole,mbEmail,mbInterest,mbLocation,mbGuard); // 아이: mbLocation이 null, 보호자: mbGuard이 null

	return "회원가입 축하 페이지";
	}
		
	// SecuritConfig에서 secured어노테이션 활성화: securedEnabled = true
	// @Secured: 권한 
//	@Secured("ROLE_ADMIN")
//	@GetMapping("/info")
//	public @ResponseBody String info() {
//		return "개인정보";
//	}

	// SecuritConfig에서 preAuthorize 어노테이션 활성화: prePostEnabled = true 
	// @PreAuthorize: 해당 메서드가 실행되기 직전에 실행 
	// 여러개 걸고 싶을 떄 hasRole 사용 
//	@PreAuthorize("hasRole('ROLE_MANAGER') or hasRole('ROLE_ADMIN')")
//	@GetMapping("/data")
//	public @ResponseBody String data() {
//		return "데이터";
//	}
//	

	
//	@GetMapping("/admin")
//	public @ResponseBody String admin() {
//		return "admin";
//	}

}
