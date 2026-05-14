app.get("/health",(req,res)=>{res.send("ok");});
let adults = 2;
let children = 0;
let rooms = 1;

// ***************SELECT MONTH ************************
/*const select = document.getElementById("monthSelect");

const months = [
    "Jan", "Feb", "Mar", "Apr", "May", "Jun",
    "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
];

let currentDate = new Date();
let currentMonth = currentDate.getMonth();
let currentYear = currentDate.getFullYear();

for(let i = 0; i < 4; i++){

    let monthIndex = (currentMonth + i) % 12;
    let year = currentYear + Math.floor((currentMonth + i) / 12);

    let option = document.createElement("option");

    option.text =
        months[monthIndex] + " " + year;

    select.add(option);
}
*/
// ***************SELECT MONTH ************************

const select = document.getElementById("monthSelect");

if(select){

    const months = [
        "Jan", "Feb", "Mar", "Apr",
        "May", "Jun", "Jul", "Aug",
        "Sep", "Oct", "Nov", "Dec"
    ];

    let currentDate = new Date();
    let currentMonth = currentDate.getMonth();
    let currentYear = currentDate.getFullYear();

    for(let i = 0; i < 4; i++){

        let monthIndex = (currentMonth + i) % 12;

        let year =
            currentYear +
            Math.floor((currentMonth + i) / 12);

        let option = document.createElement("option");

        option.text =
            months[monthIndex] + " " + year;

        select.add(option);
    }
}



// *************** SELECT GUEST ************************


function toggleGuestBox() {

    let box = document.getElementById("guestDropdown");

    if(box.style.display === "block"){
        box.style.display = "none";
    } else {
        box.style.display = "block";
    }
}


function changeCount(type, value){

    if(type === "adult"){
        adults = Math.max(1, adults + value);
        document.getElementById("adultCount").innerText = adults;
    }

    if(type === "child"){
        children = Math.max(0, children + value);
        document.getElementById("childCount").innerText = children;
    }

    if(type === "room"){
        rooms = Math.max(1, rooms + value);
        document.getElementById("roomCount").innerText = rooms;
    }

    document.getElementById("guestText").innerText =
        adults + " adults · " +
        children + " children · " +
        rooms + " rooms";
}

// *************** SENT MESSAGE HOME PAGE************************

function sendMsg() {
    let msg = document.getElementById("msg").value;

    fetch("/ai-chat?msg=" + msg)
    .then(res => res.text())
    .then(data => {
        document.getElementById("chat-box").innerHTML += 
            "<p><b>You:</b> " + msg + "</p>" +
            "<p><b>AI:</b> " + data + "</p>";
  });
}

// *************** FILL DATA MESSAGE HOME PAGE************************


function fillModalData() {

    let form = document.getElementById("quoteForm");
    // FORM VALIDATION
    if (!form.checkValidity()) {

        form.reportValidity();
        return;
    }

    // GET VALUES
    let name = document.getElementById("name").value;
    let email = document.getElementById("email").value;
    let mobile = document.getElementById("mobile").value;

    // SET MODAL DATA
    document.getElementById("modalName").innerText = name;

    document.getElementById("modalContact").innerText =
        email + " / +91 " + mobile;

    // OPEN MODAL
    let otpModal =
        new bootstrap.Modal(
            document.getElementById('otpModal')
        );

    otpModal.show();
}




// *************** VIEW DETAIL ************************
function viewdetail() {
   
}



// *************** SEARCH HOTEL BY BOOKING.COM************************

function searchBooking(city,state){

    let checkin = document.getElementById("checkin").value;
    let checkout = document.getElementById("checkout").value;

	if(checkin === ""){  
	       return;
	   }
	   if(checkout === ""){  
	       return;
	   }
     /*adults = document.getElementById("adultCount").innerText;
     children = document.getElementById("childCount").innerText;
     rooms = document.getElementById("roomCount").innerText;
*/
let adults = document.getElementById("adultCount").innerText;
let children = document.getElementById("childCount").innerText;
let rooms = document.getElementById("roomCount").innerText;
	let location =  city + ", " + state + ", India";

    let url =
        "https://www.booking.com/searchresults.html?" +
        "ss=" + encodeURIComponent(location) +
        "&checkin=" + checkin +
        "&checkout=" + checkout +
        "&group_adults=" + adults +
        "&group_children=" + children +
        "&no_rooms=" + rooms;

    window.open(url, "_blank");
}

// *************** SEARCH HOTEL BY EXPEDIA************************

function searchExpedia(city,state){

    let checkin = document.getElementById("checkin").value;
    let checkout = document.getElementById("checkout").value;
	
	if(checkin === ""){  
		       return;
		   }
		   if(checkout === ""){		      
		       return;
		   }
		   
     /*adults = document.getElementById("adultCount").innerText;
     rooms = document.getElementById("roomCount").innerText;*/
	 let adults = document.getElementById("adultCount").innerText;
	 let children = document.getElementById("childCount").innerText;
	 let rooms = document.getElementById("roomCount").innerText;
	 let location =  city + ", " + state + ", India";
    let url =
	"https://www.expedia.com/Hotel-Search?" +
	       "destination=" + encodeURIComponent(location) +
	       "&startDate=" + checkin +
	       "&endDate=" + checkout +
	       "&adults=" + adults +
	       "&rooms=" + rooms;

    window.open(url, "_blank");
}

// *************** SEARCH HOTEL BY AGODA************************

function searchAgoda(city , state){

    let checkin = document.getElementById("checkin").value;
    let checkout = document.getElementById("checkout").value;
	
	if(checkin === ""){	      
		       return;
		   }
		   if(checkout === ""){  
		       return;
		   } 
   /*  adults = document.getElementById("adultCount").innerText;
     rooms = document.getElementById("roomCount").innerText;*/
	 let adults = document.getElementById("adultCount").innerText;
	 let children = document.getElementById("childCount").innerText;
	 let rooms = document.getElementById("roomCount").innerText;
	let location = city + " " + state;
	let url =
	    "https://www.agoda.com/en-in/search?" +
	    "textToSearch=" + encodeURIComponent(location) +
	    "&checkIn=" + checkin +
	    "&checkOut=" + checkout +
	    "&rooms=" + rooms +
	    "&adults=" + adults;

    window.open(url, "_blank");
}



// *************** SHOW PASSWORD ************************
function togglePassword(inputId, iconId){

    let input = document.getElementById(inputId);

    let icon = document.getElementById(iconId);

    if(input.type === "password"){

        input.type = "text";

        icon.classList.remove("fa-eye");

        icon.classList.add("fa-eye-slash");

    }else{

        input.type = "password";

        icon.classList.remove("fa-eye-slash");

        icon.classList.add("fa-eye");
    }
}




// *************** REGISTER VALIDATION ************************
$(function(){

// User Register validation

	var $userRegister=$("#userRegister");

	$userRegister.validate({
		
		rules:{
			name:{
				required:true,
				lettersonly:true
			}
			,
			email: {
				required: true,
				space: true,
				email: true
			},
			mobileNumber: {
				required: true,
				space: true,
				numericOnly: true,
				minlength: 10,
				maxlength: 12

			},
			password: {
				required: true,
				space: true

			},
			cpassword: {
				required: true,
				space: true,
				equalTo: '#pass'

			},
			address: {
				required: true,
				all: true

			},

			city: {
				required: true,
				space: true

			},
			state: {
				required: true,


			},
			pincode: {
				required: true,
				space: true,
				numericOnly: true

			}, img: {
				required: true,
			}
			
		},
		messages:{
			name:{
				required:'name required',
				lettersonly:'invalid name'
			},
			email: {
				required: 'email name must be required',
				space: 'space not allowed',
				email: 'Invalid email'
			},
			mobileNumber: {
				required: 'mob no must be required',
				space: 'space not allowed',
				numericOnly: 'invalid mob no',
				minlength: 'min 10 digit',
				maxlength: 'max 12 digit'
			},

			password: {
				required: 'password must be required',
				space: 'space not allowed'

			},
			cpassword: {
				required: 'confirm password must be required',
				space: 'space not allowed',
				equalTo: 'password mismatch'

			},
			address: {
				required: 'address must be required',
				all: 'invalid'

			},

			city: {
				required: 'city must be required',
				space: 'space not allowed'

			},
			state: {
				required: 'state must be required',
				space: 'space not allowed'

			},
			pincode: {
				required: 'pincode must be required',
				space: 'space not allowed',
				numericOnly: 'invalid pincode'

			},
			img: {
				required: 'image required',
			}
		}
	})
	
	
// Orders Validation


// Reset Password Validation

var $resetPassword=$("#resetPassword");

$resetPassword.validate({
		
		rules:{
			password: {
				required: true,
				space: true

			},
			confirmPassword: {
				required: true,
				space: true,
				equalTo: '#pass'

			}
		},
		messages:{
		   password: {
				required: 'password must be required',
				space: 'space not allowed'

			},
			confirmPassword: {
				required: 'confirm password must be required',
				space: 'space not allowed',
				equalTo: 'password mismatch'

			}
		}	
})



	
	
	
	
})



jQuery.validator.addMethod('lettersonly', function(value, element) {
		return /^[^-\s][a-zA-Z_\s-]+$/.test(value);
	});
	
		jQuery.validator.addMethod('space', function(value, element) {
		return /^[^-\s]+$/.test(value);
	});

	jQuery.validator.addMethod('all', function(value, element) {
		return /^[^-\s][a-zA-Z0-9_,.\s-]+$/.test(value);
	});


	jQuery.validator.addMethod('numericOnly', function(value, element) {
		return /^[0-9]+$/.test(value);
	});