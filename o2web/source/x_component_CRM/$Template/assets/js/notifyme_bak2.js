// Notifications
(function($){
	'use strict';

	// Define plugin name and parameters
	$.fn.notifyMe = function($position, $type, $title, $content, $velocity, $delay){
		// Remove recent notification for appear new
		$('.notify').remove();

		// Create the content of Alert
		var close = "<a class='notify-close'>x</a>";
		var header = "<section class='notify' data-position='"+ $position +"' data-notify='" + $type + "'>" + close + "<h1>" + $title + "</h1>";
		var content =  "<div class='notify-content'>" + $content + "</div></section>";

		var notifyModel = header + content;

		$('body').prepend(notifyModel);

		var notifyHeigth = $('.notify').outerHeight();

		// Show Notification

		if($position == "bottom"){
			$('.notify').css('bottom', '-' + notifyHeigth);
			$('.notify').animate({
				bottom: '0px'
			},$velocity);

			// Close Notifications automatically
			if(typeof $delay !== 'undefined') {
				setTimeout(function(){
					$('.notify').animate({
						bottom: '-' + notifyHeigth
					},$velocity);

					// Remove item when close
					setTimeout(function(){
						$('.notify').remove();
					},$velocity + 100);
				},$delay);
			}
		}

		else if($position == "top"){
			$('.notify').css('top', '-' + notifyHeigth);
			$('.notify').animate({
				top: '0px'
			},$velocity);

			// Close Notification automatically
			if(typeof $delay !== 'undefined') {
				setTimeout(function(){
					$('.notify').animate({
						top: '-' + notifyHeigth
					},$velocity);

					// Remove item when close
					setTimeout(function(){
						$('.notify').remove();
					},$velocity + 100);
				},$delay);
			}
		}

		else if($position == "right"){
			$('.notify').css('right', '-' + notifyHeigth);
			$('.notify').animate({
				right: '0px'
			},$velocity);

			// Close Notification automatically
			if(typeof $delay !== 'undefined') {
				setTimeout(function(){
					$('.notify').animate({
						right: '-' + notifyHeigth
					},$velocity);

					// Remove item when close
					setTimeout(function(){
						$('.notify').remove();
					},$velocity + 100);
				},$delay);
			}
		}

		else if($position == "left"){
			$('.notify').css('left', '-' + notifyHeigth);
			$('.notify').animate({
				left: '0px'
			},$velocity);

			// Close Notifications automatically
			if(typeof $delay !== 'undefined') {
				setTimeout(function(){
					$('.notify').animate({
						left: '-' + notifyHeigth
					},$velocity);

					// Remove item when close
					setTimeout(function(){
						$('.notify').remove();
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
				$(this).parent('.notify').animate({
					right: '-' + notifyHeigth
				},$velocity);
			}
			else if($position == "left"){
				$(this).parent('.notify').animate({
					left: '-' + notifyHeigth
				},$velocity);
			}

			// Remove item when close
			setTimeout(function(){
				$('.notify').remove();
			},$velocity + 200);

		});


	}
}(jQuery));






