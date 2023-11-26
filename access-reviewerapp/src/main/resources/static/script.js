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
    starContainers.forEach(container => {
        const stars = container.querySelectorAll("span");
        const hiddenInput = container.nextElementSibling; // Get the hidden input field

        stars.forEach((star, index) => {
            star.addEventListener("click", () => {
                // Update the hidden input value when a star is clicked
                hiddenInput.value = index + 1;

                // Loop through the stars again to set the "active" class
                stars.forEach((s, starIndex) => {
                    s.classList.toggle("active", starIndex <= index);
                });
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

