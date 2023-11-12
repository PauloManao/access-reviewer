
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

//assign a value to each category when user click on the stars of review page
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


document.addEventListener('DOMContentLoaded', function() {
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
});


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


