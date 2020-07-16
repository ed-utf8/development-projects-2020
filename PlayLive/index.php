<?php
include 'sql/dbc.php';
?>

<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="utf-8">
        <meta http-equiv="X-UA-Compatible" content="IE=edge">
        <meta name="viewport" content="width=device-width, initial-scale=1">

        <title>PlayLive - Home</title>
    </head>
    <?php include 'includes/header.php'; ?>
    <body>
        <header class="header-img">
            <div class="overlay">
                <div class='container-fluid'>
                    <h1>St. Jude PlayLive<br/>Charity UHC - Saturday, May 6th at 4EST</h1>
                    <button type="button" class="btn btn-primary">Donate Now!</button>
                </div>
            </div>
        </header>
        <div class="progress">
            <div class="progress-bar progress-bar-striped progress-bar-animated" role="progressbar" aria-valuenow="70" aria-valuemin="0" aria-valuemax="100" style="width: 50%"></div>
        </div>

        <div class="container-fluid">
            <div class="row">
                <div class="col-md-8">
                    <div class="card">
                        <h2>What is PlayLive?</h2>
                        <p>PlayLive is a Streamed Charity event focusing on united with gamers in the St. Jude PLAY LIVE
                            global fundraising campaign to end childhood cancer. St. Jude has helped push the childhood cancer
                            survival rate from less than 20% when they opened to 80% today. They won't stop until no child dies from cancer.<br/><a href="#">Read More...</a>
                        </p>
                    </div>
                </div>
                <div class="col-md-4">
                    <div class="card">
                        <h2>Largest Donation</h2>
                        <div class="media">
                            <div class="media-left">
                                <?php
                                    $SQL = "SELECT * FROM transactions_paypal ORDER BY price DESC LIMIT 1;";
                                    $SQL = @mysqli_query($connection, $SQL);
                                    $SQLRES = mysqli_fetch_array($SQL);
                                ?>
                                <a href="#">
                                    <img class="media-object" src="https://crafatar.com/avatars/<?php echo $SQLRES['name'] ?>?size=80">
                                </a>
                            </div>
                            <div class="media-body">
                                <h4 class="media-heading"><?php echo $SQLRES['name'] ?> <strong><?php echo "$" . $SQLRES['price'] ?></strong></h4>
                                <p>"<?php echo $SQLRES['message'] ?>"</p>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <div id="signup">
            <div class="container-fluid">
                <div class="row">
                    <div class="col-md-12 col-sm-12 col-xs-12 clearfix">
                        <h3>Want to help? Donate Now!</h3>
                        <form id="register-donation">
                            <input type="text" name="email" required="" placeholder="Email Address">
                            <input type="text" name="username" required="" placeholder="Minecraft Username">
                            <input type="submit" class="btn btn-info" value="Donate">
                        </form>
                    </div>
                </div>
            </div>
        </div>
        <div class="container-fluid">
            <div class="row">
                <div class="col-md-2">
                    <div class="stats">
                        <h2><?php echo mysqli_num_rows($SQL); ?></h2>
                        <hr>
                        <p>Total number of donations!</p>
                    </div>
                </div>
                <div class="col-md-2">
                    <div class="stats">
                        <?php
                            $query = "SELECT SUM(price) AS price FROM transactions_paypal;";
                            $res = @mysqli_query($connection, $query);
                            $res = mysqli_fetch_array($res);
                        ?>
                        <h2><?php echo "$" . $res['price'];?></h2>
                        <hr>
                        <p>Total amount raised so far!</p>
                    </div>
                </div>
                <div class="col-md-4">
                    <div class="card">
                        <h2>Why St. Jude?</h2>
                        <p>Lorem ipsum dolor sit amet, consectetur adipiscing elit. Duis a ante facilisis, aliquam ipsum ultricies, rutrum enim. Pellentesque habitant morbi tristique senectu. Quisque dapibus tincidunt ipsum sed bibendum.</p>
                    </div>
                </div>
                <div class="col-md-4">
                    <div class="card">
                        <h2>Most Recent Donation</h2>
                        <div class="media">
                            <?php
                                $query = "SELECT * FROM transactions_paypal ORDER BY id DESC LIMIT 3;";
                                $res = @mysqli_query($connection, $query);
                                while ($row = mysqli_fetch_array($res)) {
                                    $name = $row['name'];
                                    $price = $row['price'];
                                    $message = $row['message'];
                                    echo "<div class='media-left'>
                                        <a href='#'>
                                            <img class='media-object' src='https://crafatar.com/avatars/$name?size=80'>
                                        </a>
                                    </div>
                                    <div class='media-body'>
                                        <h4 class='media-heading'>$name <strong>$$price</strong></h4>
                                        <p>'$message'</p>
                                    </div>";
                                }
                            ?>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </body>
    <footer>
        <?php include 'includes/footer.php'; ?>
    </footer>
</html>
