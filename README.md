# wrdoclet
This doclet tries to generate API doc for Spring MVC service.

supported example:
	@RequestMapping(value = "/add", method = RequestMethod.POST)
	public @ResponseBody
	Student addStudent(@RequestBody Student student) {
		return student;
	}	
	
unsupported example:
    @RequestMapping("/index/{username}")
    public String index(@PathVariable("username") String username) {
        System.out.print(username);
        return "index";
    }
	
	@RequestMapping(value = "/delete/{name}", method = RequestMethod.POST)
	public @ResponseBody
	String delStudent(@RequestBody Student student,
			@PathVariable("name") String username) {
		return "delete name:" + username + "/" + student.getName();
	}

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public String login2(HttpServletRequest request) {
            String username = request.getParameter("username").trim();
        return "login2";
    }
	
	@RequestMapping(value = "login", method = RequestMethod.POST)
	public String testParam(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
		String username = request.getParameter("username");
		return null;
	}
	
	@RequestMapping(value = "login", method = RequestMethod.POST)
	public String testParam(PrintWriter out, @RequestParam("username") String username) {
		out.println(username);
		return null;
	}
	
	@RequestMapping(value = "login", method = RequestMethod.POST)
	public String testParam(PrintWriter out, User user) {
		out.println(user.getUsername());
		return null;
	}

	@RequestMapping(value = "login", method = RequestMethod.POST)
	public String testParam(User user, Map model) {
		model.put("user",user);
		return "view";
	}
	
	...
	...