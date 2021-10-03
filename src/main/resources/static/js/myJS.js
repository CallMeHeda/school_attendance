function post(path, params, method) {
	method = method || "post"; 
	var form = document.createElement("form");
	form.setAttribute("method", method);
	form.setAttribute("action", path);
	
	for ( var key in params) {
		if (params.hasOwnProperty(key)) {
			var hiddenField = document.createElement("input");
			hiddenField.setAttribute("type", "hidden");
			hiddenField.setAttribute("name", key);
			hiddenField.setAttribute("value", params[key]);
			form.appendChild(hiddenField);
		}
	}

	document.body.appendChild(form);
	form.submit();
}

$(document).ready(function () {
// TYPING ANIMATION
(function ($) {
		$.fn.writeText = function(content) {
			var contentArray = content.split(""), current = 0, elem = this;
			setInterval(function() {
				if (current < contentArray.length) {
					elem.text(elem.text() + contentArray[current++]);
				}
			}, 130);
		};
	})(jQuery);
	// texte qui apparait dans l'animation écriture au clavier
	$("#typing").writeText("Bienvenu(e) au cours de PID (VO)");
});