package net.nilsghesquiere.web;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import net.nilsghesquiere.entities.Jeugdhuis;
import net.nilsghesquiere.entities.Role;
import net.nilsghesquiere.entities.User;
import net.nilsghesquiere.services.JeugdhuisService;
import net.nilsghesquiere.services.RoleService;
import net.nilsghesquiere.services.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/")
public class IndexController {
	private static final Logger logger = Logger.getLogger(IndexController.class.getSimpleName());
	private static final String VIEW = "index";
	private static final String REDIRECT_NA_INCREMENT = "redirect:/";
	private final JeugdhuisService jeugdhuisService ;
	private final UserService userService ;
	private final RoleService roleService;
	private static boolean testDataCreated = false;
	

	@Autowired
	public IndexController(JeugdhuisService jeugdhuisService, UserService userService, RoleService authorityService) {
		this.jeugdhuisService = jeugdhuisService;
		this.userService = userService;
		this.roleService = authorityService;
	}

	@RequestMapping(method = RequestMethod.GET)
	ModelAndView Jeugdhuis() {
		if (!testDataCreated){
			Role jhadmin = new Role("jhadmin");
			Role appadmin = new Role("appadmin");
			roleService.create(jhadmin);
			roleService.create(appadmin);
			Set<Role> rolesNils = new HashSet<>();
			Set<Role> rolesLukas = new HashSet<>();
			rolesNils.add(jhadmin);
			rolesNils.add(appadmin);
			rolesLukas.add(jhadmin);
	    	User nils = new User("Nils","test123",rolesNils);
	    	User lukas = new User("Lukas","test123",rolesLukas);
	    	userService.create(nils);
	    	userService.create(lukas);
	    	Jeugdhuis dentraveir = new Jeugdhuis("Traveir",313,nils);
	    	Jeugdhuis chaplin = new Jeugdhuis("Chaplin",17,lukas);
	    	jeugdhuisService.create(dentraveir);
	    	jeugdhuisService.create(chaplin);
			logger.info("filled database with testdata.");
			testDataCreated = true;
		}
		
		//Change this to get the user object instead
		String currentUser = "";
		if (SecurityContextHolder.getContext().getAuthentication() != null &&
				 SecurityContextHolder.getContext().getAuthentication().isAuthenticated()){
			currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
		} 

		logger.info("Loading index page.");
		List<Jeugdhuis> jeugdhuizen = jeugdhuisService.findAll();
		return new ModelAndView(VIEW).addObject("jeugdhuizen", jeugdhuizen).addObject("currentUser", currentUser);
	}
	
	@RequestMapping(method = RequestMethod.POST)
	ModelAndView IncreaseDranken(@RequestParam Long jhId, @RequestParam Integer aantDr) {
		Jeugdhuis jeugdhuis = jeugdhuisService.read(jhId);
		logger.info("Increasing dranken voor " + jeugdhuis.getName());
		jeugdhuis.incrementAantalDranken(aantDr);
		jeugdhuisService.update(jeugdhuis);
		return new ModelAndView(REDIRECT_NA_INCREMENT);
	}
	
	  // Login form
	  @RequestMapping("/login.html")
	  public String login() {
	    return "login";
	  }

	  // Login form with error
	  @RequestMapping("/login-error.html")
	  public String loginError(Model model) {
	    model.addAttribute("loginError", true);
	    return "login";
	  }
}