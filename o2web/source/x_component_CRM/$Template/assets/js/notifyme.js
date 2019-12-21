// Notifications
(function($){
	'use strict';

	// Define plugin name and parameters
	$.fn.notifyMe = function($position, $type, $title, $button, $more, $content, $id, $velocity, $json, $delay){
		// Remove recent notification for appear new
		var idStr = "#"+$id;
		$(idStr).remove();
		// Create the content of Alert
		var close = "<a class='notify-close'>x</a>";
		var header = "<section class='notify' data-position='"+ $position +"' data-notify='" + $type +"' id='" + $id+ "'>" + close + "<h3>" + $title + "</h3>";
		var content =  "<div class='notify-content'>" + $content + "</div></section>";

		var notifyModel = header +$button+$more+ content;

		//$('body').prepend(notifyModel);
		$('body').append(notifyModel);

		//var notifyHeigth = $('.notify').outerHeight();
		var notifyHeigth = $(idStr).outerWidth()+"px";
		// Show Notification
		 if($position == "right"){
			$(idStr).css('right', '-' + notifyHeigth);
			/*if($button==""){
				$(idStr).animate({
					right: '0px',
					top: '0'
				},$velocity);
				$(idStr).css('width', '1100px');
			}else{
				$(idStr).animate({
					right: '0px'
				},$velocity);
			}*/
			 if(typeof $json !== 'undefined') {
				 $(idStr).animate({
					 right: $json.right,
					 top: $json.top
				 },$velocity);
			 }else{
				 $(idStr).animate({
					 right: '0px'
				 },$velocity);
			 }


			// Close Notification automatically
			if(typeof $delay !== 'undefined') {
				setTimeout(function(){
					$('.notify').animate({
						right: '-' + notifyHeigth
					},$velocity);

					// Remove item when close
					setTimeout(function(){
						$(idStr).remove();
					},$velocity + 100);
				},$delay);
			}
		}
		else if($position == "left"){
			$(idStr).css('left', '-' + notifyHeigth);
			$(idStr).animate({
				left: '30%'
			},$velocity);

			// Close Notifications automatically
			if(typeof $delay !== 'undefined') {
				setTimeout(function(){
					$('.notify').animate({
						left: '-' + notifyHeigth
					},$velocity);

					// Remove item when close
					setTimeout(function(){
						$(idStr).remove();
					},$velocity + 100);
				},$delay);
			}
		}

		// Close Notification
		$('.notify-close').click(function(){
			// Move notification
			if($position == "bottom"){
				$(this).parent('.notify').animate({
					bottom: '-' + notifyHeigth
				},$velocity);
			}
			else if($position == "top"){
				$(this).parent('.notify').animate({
					top: '-' + notifyHeigth
				},$velocity);
			}
			else if($position == "right"){
				$(this).parent(idStr).animate({
					right: '-' + notifyHeigth
				},$velocity);
			}
			else if($position == "left"){
				$(this).parent(idStr).animate({
					left: '-' + notifyHeigth
				},$velocity);
			}

			// Remove item when close
			setTimeout(function(){
				$(idStr).remove();
				if($(".mask").length>0){
					$(".mask").attr("style",'left: 0px; top: 0px; width: 100%; overflow: hidden; position: absolute; z-index: 500000; background-color: rgb(255, 255, 255)');
					$(".mask").attr("class","");
				}
			},$velocity + 200);

		});


	}
}(jQuery));






