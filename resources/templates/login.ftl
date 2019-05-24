<!doctype html>
<html lang="en">
<head>
    <!-- Required meta tags -->
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">

    <!-- Bootstrap CSS -->
    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.1.3/css/bootstrap.min.css" integrity="sha384-MCw98/SFnGE8fJT3GXwEOngsV7Zt27NXFoaoApmYm81iuXoPkFOJwJ8ERdknLPMO" crossorigin="anonymous">

    <title>Hello, world!</title>
</head>
<body>
<div class="container">
    <div class="row align-items-center" style="min-height: 100vh">
        <div class="col col-lg-2"></div>
        <div class="col col-auto container"  >
            <form class="card" action="/login" encType = "application/x-www-form-urlencoded" method="POST">
                <h5 class="card-header text-center">
                    Log In
                </h5>
                <div class="card-body">
                    <div class="form-group row">
                        <label for="text" class="col-4 col-form-label">Name</label>
                        <div class="col-8">
                            <input for="name" type="text" class="form-control" id="inputEmail" placeholder="Email" name="name">
                        </div>
                    </div>
                    <div class="form-group row">
                        <label for="password" class="col-4 col-form-label">Password</label>
                        <div class="col-8">
                            <input for="password" type="password" class="form-control" id="inputPassword" placeholder="Password" name="password">
                        </div>
                    </div>

                    <div class="form-group row justify-content-center">
                        <div class="col-12">
                            <button type="submit" class="btn btn-primary btn-block" value="Login">Sign in</button>
                        </div>
                    </div>
                </div>
            </form>
        </div>
        <div class="col col-lg-2"></div>
    </div>
</div>

<!-- Optional JavaScript -->
<!-- jQuery first, then Popper.js, then Bootstrap JS -->
<script src="https://code.jquery.com/jquery-3.2.1.slim.min.js" integrity="sha384-KJ3o2DKtIkvYIK3UENzmM7KCkRr/rE9/Qpg6aAZGJwFDMVNA/GpGFF93hXpG5KkN" crossorigin="anonymous"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.12.9/umd/popper.min.js" integrity="sha384-ApNbgh9B+Y1QKtv3Rn7W3mgPxhU9K/ScQsAP7hUibX39j7fakFPskvXusvfa0b4Q" crossorigin="anonymous"></script>
<script src="https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0/js/bootstrap.min.js" integrity="sha384-JZR6Spejh4U02d8jOt6vLEHfe/JQGiRRSQQxSfFWpi1MquVdAyjUar5+76PVCmYl" crossorigin="anonymous"></script>

</body>
</html>