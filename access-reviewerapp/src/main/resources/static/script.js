/* 
    index page
*/

    document.addEventListener('DOMContentLoaded', function() {
        // Function to handle the geocoding of the address
        function geocodeAddress() {
            // Address list style in the index page
            document.querySelector('.address-container').style.display = 'flex';               
            document.querySelector('.search-container').style.marginBottom = '0';
            document.querySelector('.search-bar').style.borderBottomLeftRadius = '0';
            document.querySelector('.search-bar').style.borderBottomRightRadius = '0';
            document.querySelector('.search-bar').style.borderBottomStyle = 'solid';
            document.querySelector('.search-bar').style.borderBottomWidth = '0.5px';
            document.querySelector('.address-list').style.borderTopLeftRadius = '0';
            document.querySelector('.address-list').style.borderTopRightRadius = '0';
        	
            var query = document.getElementById('searchTbx').value;
            var url = `/geocode?query=${encodeURIComponent(query)}`;
    
            fetch(url)
                .then(response => response.json())
                .then(data => {
                    var outputDiv = document.getElementById('output');
                    outputDiv.innerHTML = '';
    
                    if (data && data.length > 0) {
                        data.forEach(result => {
                            var addressDiv = document.createElement('div');
                            addressDiv.textContent = result.display_name;
                            addressDiv.style.cursor = "pointer";
                            addressDiv.addEventListener('click', function() {
                                openAddressPage(result.display_name, result.lat, result.lon);
                            });
                            outputDiv.appendChild(addressDiv);
                        });
                    } else {
                        outputDiv.textContent = "Locations not found.";
                    }
                })
                .catch(error => {
                    console.error("Error fetching location data:", error);
                });
        }

        // Function to open a new webpage with the location details
        function openAddressPage(address, latitude, longitude) {
            const queryString = `?address=${encodeURIComponent(address)}&lat=${latitude}&lon=${longitude}`;
            window.location.href = `/details.html${queryString}`;
        }

        // Enable pressing Enter to perform the search
        var searchTextBox = document.getElementById('searchTbx');
    if (searchTextBox) {
        searchTextBox.addEventListener('keydown', function(event) {
            if (event.key === 'Enter') {
                event.preventDefault(); // Prevent the default form submission
                geocodeAddress(); // Call the geocodeAddress function
            }
        });
    }

        // Attach the click event to the search button
    var searchButton = document.querySelector('.search-bar button');
    if (searchButton) {
        searchButton.addEventListener('click', function() {
            geocodeAddress();
        });
    }
        
        
    });


/* 
    login page
*/
document.addEventListener('DOMContentLoaded', function() {
		
	window.onload = function() {
	    const stayOnSignupElement = document.getElementById("stay-on-signup");
	    if (stayOnSignupElement) {
	        const stayOnSignup = stayOnSignupElement.value;
	        if (stayOnSignup === 'true') {
	            formSignIn.classList.add("hide");
	            formSignUp.classList.remove("hide");
	        }
	    }
	};
	const btnSignIn = document.getElementById("sign-in");
	const btnSignUp = document.getElementById("sign-up");
	const formSignUp = document.querySelector(".signup");
	const formSignIn = document.querySelector(".signin");
	
	if(btnSignIn){
	    btnSignIn.addEventListener("click", e =>{
	        formSignUp.classList.add("hide");
	        formSignIn.classList.remove("hide")
	    })
	}
	
	if(btnSignUp){
	    btnSignUp.addEventListener("click", e =>{
	        formSignIn.classList.add("hide");
	        formSignUp.classList.remove("hide")
	    })
	}
});



/* 
    Location Details page
*/

document.addEventListener('DOMContentLoaded', function() {
	
	function setupSwipeScrolling() {
	    const imageContainer = document.querySelector('.image-place');
	    let isDown = false;
	    let startX;
	    let scrollLeft;
	
	    imageContainer.addEventListener('mousedown', (e) => {
	        isDown = true;
	        startX = e.pageX - imageContainer.offsetLeft;
	        scrollLeft = imageContainer.scrollLeft;
	    });
	
	    imageContainer.addEventListener('mouseleave', () => {
	        isDown = false;
	    });
	
	    imageContainer.addEventListener('mouseup', () => {
	        isDown = false;
	    });
	
	    imageContainer.addEventListener('mousemove', (e) => {
	        if (!isDown) return;
	        e.preventDefault();
	        const x = e.pageX - imageContainer.offsetLeft;
	        const walk = (x - startX) * 2; // Speed of scroll
	        imageContainer.scrollLeft = scrollLeft - walk;
	    });
	}
		
	//load images
	    function loadImagesForAddress(addressString) {
        fetch(`/images?addressString=${addressString}`)
            .then(response => response.json())
            .then(imageUrls => {
                const imageContainer = document.querySelector('.image-place');
                imageUrls.forEach(url => {
                    const img = document.createElement('img');
                    img.src = url;
                    img.classList.add('location-image');
                    img.addEventListener('click', () => openImagePopup(url));
                    imageContainer.appendChild(img);
                });
                setupSwipeScrolling();
            });
    }
    
	function openImagePopup(url) {
	    const popup = document.createElement('div');
	    popup.classList.add('image-popup');
	
	    const content = document.createElement('div'); // New container for image and button
	    content.classList.add('popup-content');
	    popup.appendChild(content);
	    
	    const img = document.createElement('img');
	    img.src = url;
	    img.classList.add('popup-image');
	    content.appendChild(img);
	
	    const closeBtn = document.createElement('button');
	    closeBtn.textContent = 'Close';
	    closeBtn.classList.add('close-popup');
	    closeBtn.addEventListener('click', () => popup.remove());
	    content.appendChild(closeBtn);
	
	    document.body.appendChild(popup);
	}

    // Function to send report
	function sendReport(reviewId, reportReason) {
	    fetch('/reportReview', {
	        method: 'POST',
	        headers: { 
	            'Content-Type': 'application/json',
	            'Accept': 'text/plain' // Expecting plain text response
	        },
	        body: JSON.stringify({ reviewId, reason: reportReason })
	    })
	    .then(response => {
	        if (!response.ok) {
	            throw new Error('Error reporting review');
	        }
	        return response.text(); // Handling text response
	    })
	    .then(message => {
	        alert(message); // Display the success message
	    })
	    .catch(error => {
	        console.error('Error:', error);
	    });
	}
	
	// Function to check if the user is authenticated
	async function isAuthenticated() {
	    try {
	        const response = await fetch('/api/isAuthenticated');
	        return await response.json();
	    } catch (error) {
	        console.error('Error:', error);
	        return false;
	    }
	}
	
	// Function to show a small pop-up for reporting
async function showSmallReportPopup(reviewId, reportIcon) {
    try {
        const isAuth = await isAuthenticated();
        if (!isAuth) {
            alert('You need to be signed in to report a review.');
            return;
        }

        const smallPopup = document.createElement('div');
        smallPopup.classList.add('small-report-popup');
        smallPopup.textContent = 'Report';
        smallPopup.onclick = () => {
            smallPopup.remove();
            showReportOptions(reviewId);
        };

        // Position the small pop-up near the report icon
        const iconRect = reportIcon.getBoundingClientRect();
        smallPopup.style.top = `${iconRect.bottom}px`;
        smallPopup.style.left = `${iconRect.left}px`;

        document.body.appendChild(smallPopup);

        // Function to handle outside click
        function handleOutsideClick(event) {
            if (!smallPopup.contains(event.target) && !reportIcon.contains(event.target)) {
                smallPopup.remove();
                document.removeEventListener('click', handleOutsideClick);
            }
        }

        // Add event listener to document
        setTimeout(() => { // Timeout ensures that the event listener is not executed immediately
            document.addEventListener('click', handleOutsideClick);
        }, 0);
    } catch (error) {
        console.error('Error:', error);
    }
}

	// Function to fetch weather data
		function fetchWeather(latitude, longitude, address) {
			const apiUrl = `/weather?lat=${latitude}&lon=${longitude}`;
			fetch(apiUrl)
			.then(response => response.json())
			.then(weatherData => {
				const temperature = (weatherData.main.temp - 273.15).toFixed(2);
				const weatherConditions = weatherData.weather[0].description;
				const addressText = document.getElementById("addressText");
				addressText.textContent = `Address: ${address}`;
				const weatherInfo = document.getElementById("weatherInfo");
				weatherInfo.textContent = `Temperature: ${temperature}°C, Conditions: ${weatherConditions}`;
			})
			.catch(error => {
				console.error("Error fetching weather data:", error);
			});
		}
		
		// Function to navigate to the review page with the selected address
		function navigateToReviewPage(address) {
			window.location.href = `/review?address=${encodeURIComponent(address)}`;
		}
		
				//fetch reviews
		function fetchReviews(address) {
		    const apiUrl = `/comments?addressString=${encodeURIComponent(address)}`; // Corrected parameter name
		    fetch(apiUrl)
		        .then(response => {
		            if (!response.ok && response.status === 204) {
		                throw new Error('No reviews found for this address');
		            }
		            return response.json();
		        })
		        .then(reviews => {
		            displayReviews(reviews);
		        })
		        .catch(error => {
		            console.error("Error fetching reviews:", error);
		            document.getElementById("previousreviews").textContent = "No reviews available for this address.";
		        });
		}
		
		 // Function to display reviews	 
	function displayReviews(reviews) {
	    const reviewsContainer = document.getElementById("previousreviews");
	    reviewsContainer.innerHTML = ""; // Clear previous reviews
	
	    reviews.forEach(reviewDto => {
	        const reviewElement = document.createElement("div");
	        reviewElement.classList.add('review');
	
	        const commentsParagraph = document.createElement("p");
	        commentsParagraph.textContent = reviewDto.comments;
	
	        const userSpan = document.createElement("span");
	        userSpan.textContent = ` by ${reviewDto.username}`; // Display username
	        commentsParagraph.appendChild(userSpan);
	
	        reviewElement.appendChild(commentsParagraph);
	        
	        
	        // Add three-dots icon for reporting
	        const reportIcon = document.createElement("div");
	        reportIcon.classList.add('report-icon');
	        reportIcon.innerHTML = '...'; // Three dots
	        reportIcon.onclick = () => showSmallReportPopup(reviewDto.id, reportIcon); // Add onClick handler
	        reviewElement.appendChild(reportIcon);
	        
	        reviewsContainer.appendChild(reviewElement);
	    });
	}
	
	//pop-up with a list of the reasons for reporting a review
	function showReportOptions(reviewId) {
	
	    // Container for the entire pop-up
	    const popupContainer = document.createElement('div');
	    popupContainer.classList.add('report-popup-container');
	    
	        // Clicking outside the pop-up to close it
	    popupContainer.addEventListener('click', function(event) {
	        if (event.target === popupContainer) {
	            popupContainer.remove();
	        }
	    });
	
	    // Create a pop-up container
	    const reportPopup = document.createElement('div');
	    reportPopup.classList.add('report-popup');
	    
	        // Create a close button (X) at the top right corner of the pop-up
	    const closeButton = document.createElement('button');
	    closeButton.textContent = 'X';
	    closeButton.classList.add('close-button');
	    closeButton.onclick = () => popupContainer.remove();
	    reportPopup.appendChild(closeButton);
	
	    // Create a title for the pop-up
	    const title = document.createElement('h3');
	    title.textContent = 'Report Review';
	    reportPopup.appendChild(title);
	
	    // Reasons for reporting
	    const reasons = ["Inappropriate Language", "Spam or Advertising", "Harassment or Bullying", "False Information", "Off-topic Content", "Other"];
	    
	    // Create a form
	    const form = document.createElement('form');
	    form.id = 'reportForm';
	
	    reasons.forEach((reason, index) => {
	        const container = document.createElement('div');
	        container.classList.add('radio-container');
	
	        const radioInput = document.createElement('input');
	        radioInput.type = 'radio';
	        radioInput.id = 'reason' + index;
	        radioInput.name = 'reportReason';
	        radioInput.value = reason;
	
	        const label = document.createElement('label');
	        label.htmlFor = 'reason' + index;
	        label.textContent = reason;
	
	        container.appendChild(radioInput);
	        container.appendChild(label);
	        form.appendChild(container);
	    });
	
	    reportPopup.appendChild(form);
	    
	    
	
	    // Submit button
	    const submitButton = document.createElement('button');
	    submitButton.textContent = 'Submit Report';
	    submitButton.type = 'button';
	    submitButton.onclick = () => {
	        const selectedReason = document.querySelector('input[name="reportReason"]:checked').value;
	        sendReport(reviewId, selectedReason);
	        popupContainer.remove(); // Remove the entire pop-up container after submission
	    };
	    reportPopup.appendChild(submitButton);
	    
	
	    // Append the pop-up to the container, then append the container to the body
	    popupContainer.appendChild(reportPopup);
	    document.body.appendChild(popupContainer);
	}
		
		// Event listener for 'Write a Review' button
			var writeReviewBtn = document.getElementById("writeReviewBtn");
			
			if (writeReviewBtn) {
				document.getElementById("writeReviewBtn").addEventListener('click', function() {
				const addressText = document.getElementById("addressText").textContent;
				navigateToReviewPage(addressText);
				});
			}
		
		// Extract and use query parameters for weather and reviews fetching
		const queryString = window.location.search;
		const urlParams = new URLSearchParams(queryString);
		const address = decodeURIComponent(urlParams.get("address"));
		const latitude = parseFloat(urlParams.get("lat"));
		const longitude = parseFloat(urlParams.get("lon"));
		
		    if (address && !isNaN(latitude) && !isNaN(longitude)) {
        fetchWeather(latitude, longitude, address);
        fetchReviews(address);
        loadImagesForAddress(address);
    }
	
});




/* 
    Review page
*/

document.addEventListener('DOMContentLoaded', function() {
    // Function to extract and format the address from the query parameter
    function getAddressFromQueryParameter() {
        const queryString = window.location.search;
        const urlParams = new URLSearchParams(queryString);
        let address = urlParams.get("address");

        // Check if the address starts with 'Address: ' and remove it
        if (address && address.startsWith("Address: ")) {
            address = address.substring(9); // 'Address: ' is 9 characters long
        }

        return address;
    }
    
    // Display the formatted address from the query parameter
    const address = getAddressFromQueryParameter();
    if (address) {
        const addressText = document.getElementById("addressText");
        const addressInput = document.getElementById("addressInput");

        // Set the address text and populate the hidden input
        addressText.textContent = decodeURIComponent(address);
        addressInput.value = decodeURIComponent(address);
    }

    // Star rating functionality
    const starContainers = document.querySelectorAll(".rating-box [class^='stars']");
	const ratingLabels = ["Terrible", "Poor", "Average", "Very good", "Excellent"]; // Updated variable name
	
	starContainers.forEach(container => {
	    const stars = container.querySelectorAll("span");
	    const hiddenInput = container.parentNode.querySelector("input[type='hidden']");
	    const ratingLabel = container.parentNode.querySelector(".rating-label");
	
	    stars.forEach((star, index) => {
	        star.addEventListener("click", () => {
	            hiddenInput.value = index + 1;
	            ratingLabel.textContent = ratingLabels[index]; // Use ratingLabels here
	            stars.forEach((s, starIndex) => {
	                s.classList.toggle("active", starIndex <= index);
	            });
	        });
	
	        star.addEventListener("mouseover", () => {
	            ratingLabel.textContent = ratingLabels[index]; // Use ratingLabels here
	            stars.forEach((s, starIndex) => {
	                s.classList.toggle("hover", starIndex <= index);
	            });
	        });
	
	        star.addEventListener("mouseout", () => {
	            stars.forEach(s => {
	                s.classList.remove("hover");
	            });
	            // Reset the label text based on the hidden input's value
	            ratingLabel.textContent = hiddenInput.value > 0 ? ratingLabels[hiddenInput.value - 1] : '';
	        });
	    });
	});

    // Form submission logic
    var reviewForm = document.querySelector('.review-form');
    if (reviewForm) {
        reviewForm.addEventListener('submit', function(event) {
            event.preventDefault(); // Prevent default form submission behavior

            // Check if all categories are rated and the textarea has enough characters
            if (!areAllCategoriesRated() || !isTextareaValid()) {
                return; // Show message and stop here
            }

            // Check if the user is logged in
            fetch('/api/isAuthenticated')
                .then(response => response.json())
                .then(isAuthenticated => {
                    if (isAuthenticated) {
                        this.submit(); // User is authenticated, submit the form
                    } else {
                        showLoginLink(); // User is not authenticated, show login link
                    }
                })
                .catch(error => {
                    document.getElementById('message').textContent = 'Error checking login status';
                });
        });
    }

    // Function to check if all categories are rated
    function areAllCategoriesRated() {
        const allRatings = document.querySelectorAll('.rating-box input[type=hidden]');
        let allRated = true;

        allRatings.forEach(input => {
            if (input.value === "0") {
                allRated = false;
            }
        });

        if (!allRated) {
            document.getElementById('message').textContent = "Please rate all categories before submitting.";
        }

        return allRated;
    }

    // Function to check if textarea is valid
    function isTextareaValid() {
        const textarea = document.querySelector('textarea');
        if (textarea.value.length < 20) {
            document.getElementById('message').textContent = `Please enter at least 20 characters. You've entered ${textarea.value.length}.`;
            return false;
        }
        return true;
    }

    // Function to show login link
    function showLoginLink() {
        var messageElement = document.getElementById('message');
        messageElement.innerHTML = '';

        var loginLink = document.createElement('a');
        loginLink.href = '/registration';
        loginLink.textContent = 'Please, log in';

        messageElement.appendChild(loginLink);
    }
});



/* 
    admin_users
*/

function toggleUser(userId, element) {
    var status = element.getAttribute('data-status') === 'true';
    var action = status ? 'disable' : 'enable';

    fetch(`/admin/users/${action}/${userId}`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            // Add your CSRF token here if necessary
        },
        // body: JSON.stringify({ your data if needed })
    })
    .then(response => response.json())
    .then(data => {
        if(data.success) {
            element.setAttribute('data-status', data.newStatus);
            element.src = data.newStatus ? '/path/to/enabled_icon.png' : '/path/to/disabled_icon.png';
            // Update the toggle animation as necessary
        } else {
            console.error('Failed to update user status');
        }
    })
    .catch(error => {
        console.error('Error:', error);
    });
}



/*!
* Start Bootstrap - Agency v7.0.12 (https://startbootstrap.com/theme/agency)
* Copyright 2013-2023 Start Bootstrap
* Licensed under MIT (https://github.com/StartBootstrap/startbootstrap-agency/blob/master/LICENSE)
*/
//
// Scripts
// 

window.addEventListener('DOMContentLoaded', event => {

    // Navbar shrink function
    var navbarShrink = function () {
        const navbarCollapsible = document.body.querySelector('#mainNav');
        if (!navbarCollapsible) {
            return;
        }
        if (window.scrollY === 0) {
            navbarCollapsible.classList.remove('navbar-shrink')
        } else {
            navbarCollapsible.classList.add('navbar-shrink')
        }

    };

    // Shrink the navbar 
    navbarShrink();

    // Shrink the navbar when page is scrolled
    document.addEventListener('scroll', navbarShrink);

    //  Activate Bootstrap scrollspy on the main nav element
    const mainNav = document.body.querySelector('#mainNav');
    if (mainNav) {
        new bootstrap.ScrollSpy(document.body, {
            target: '#mainNav',
            rootMargin: '0px 0px -40%',
        });
    };

    // Collapse responsive navbar when toggler is visible
    const navbarToggler = document.body.querySelector('.navbar-toggler');
    const responsiveNavItems = [].slice.call(
        document.querySelectorAll('#navbarResponsive .nav-link')
    );
    responsiveNavItems.map(function (responsiveNavItem) {
        responsiveNavItem.addEventListener('click', () => {
            if (window.getComputedStyle(navbarToggler).display !== 'none') {
                navbarToggler.click();
            }
        });
    });

});

