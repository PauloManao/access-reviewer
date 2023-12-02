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
            window.open(`/details.html${queryString}`, "_blank");
        }

        // Enable pressing Enter to perform the search
        document.getElementById('searchTbx').addEventListener('keydown', function(event) {
            if (event.key === 'Enter') {
                event.preventDefault(); // Prevent the default form submission
                geocodeAddress(); // Call the geocodeAddress function
            }
        });

        // Attach the click event to the search button
        document.querySelector('.search-bar button').addEventListener('click', function() {
            geocodeAddress();
        });
    });

/* 
    login page
*/
window.onload = function() {
    const stayOnSignup = document.getElementById("stay-on-signup").value;
    if (stayOnSignup === 'true') {
        formSignIn.classList.add("hide");
        formSignUp.classList.remove("hide");
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


/* 
    News page
*/

const btnHam = document.querySelector('.ham-btn');
const btnTimes = document.querySelector('.times-btn');
const navBar = document.getElementById('nav-bar');

if(btnHam){
    btnHam.addEventListener('click', function(){
        if(btnHam.className !== ""){
            btnHam.style.display = "none";
            btnTimes.style.display = "block";
            navBar.classList.add("show-nav");
        }
    });
}

if(btnTimes){
    btnTimes.addEventListener('click', function(){
        if(btnHam.className !== ""){
            this.style.display = "none";
            btnHam.style.display = "block";
            navBar.classList.remove("show-nav");
        }
    });
}


/* 
    Location Details page
*/

document.addEventListener('DOMContentLoaded', function() {

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
        reviewsContainer.appendChild(reviewElement);
    });
}
		
		// Event listener for 'Write a Review' button
				document.getElementById("writeReviewBtn").addEventListener('click', function() {
			const addressText = document.getElementById("addressText").textContent;
			navigateToReviewPage(addressText);
		});
		
		// Extract and use query parameters for weather and reviews fetching
		const queryString = window.location.search;
		const urlParams = new URLSearchParams(queryString);
		const address = decodeURIComponent(urlParams.get("address"));
		const latitude = parseFloat(urlParams.get("lat"));
		const longitude = parseFloat(urlParams.get("lon"));
		
		    if (address && !isNaN(latitude) && !isNaN(longitude)) {
        fetchWeather(latitude, longitude, address);
        fetchReviews(address);
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

