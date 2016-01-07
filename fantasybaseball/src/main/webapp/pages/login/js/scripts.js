
jQuery(document).ready(function() {
	
    /*
        Fullscreen background
    */
    $.backstretch("pages/login/img/backgrounds/baseball-stadium-blur.jpg");
    
    /*
        Form validation
    */
    $('.login-form input[type="text"], .login-form input[type="password"], .login-form textarea').on('focus', function() {
    	$(this).removeClass('input-error');
    });
    
    
    /*
    $('.login-form').on('submit', function(e) {
    	
    	var email = document.querySelector('#email').value;
    	var password = document.querySelector('#password').value;
    	
    	google.appengine.samples.hello.logon(email, password);


		if( email == "" || password == "") {
	    	$(this).find('input[type="text"], input[type="password"], textarea').each(function(){
	    		if( $(this).val() == "" ) {
	    			e.preventDefault();
	    			$(this).addClass('input-error');
	    		}
	    		else {
	    			$(this).removeClass('input-error');
	    		}
	    	});
		}
		else {
		    google.appengine.samples.hello.logon(email, password);
		}
		

    	
    	$(this).find('input[type="text"], input[type="password"], textarea').each(function(){
    		if( $(this).val() == "" ) {
    			e.preventDefault();
    			$(this).addClass('input-error');
    		}
    		else {
    			$(this).removeClass('input-error');
    		}
    	});
    	

    });
*/
    
    $("#submit_button").click(function()
    {
    	var email = document.querySelector('#email').value;
    	var password = document.querySelector('#password').value;

		if( email == "" || password == "") {
			$('.login-form').find('input[type="text"], input[type="password"], textarea').each(function(){
	    		if( $(this).val() == "" ) {
	    			$(this).addClass('input-error');
	    		}
	    		else {
	    			$(this).removeClass('input-error');
	    		}
	    	});
		}
		else {
		    google.appengine.samples.hello.logon(email, password);
		}
		
 
    });
    

});
