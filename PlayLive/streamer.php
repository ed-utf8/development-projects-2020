<?php
include 'sql/dbc.php';

$query = "SELECT * FROM streamers;";
$res = @mysqli_query($connection, $query);
$streamers = array(mysqli_fetch_array($res));
$random = array_rand($streamers);
$streamers = $streamers[$streamers];

$id = $streamers['id'];

$query = "SELECT * FROM streamers WHERE id = '$id';";
$res = @mysqli_query($connection, $query);
$row = mysqli_fetch_array($res);
$name = $row['ign'];
$link = $row['link'];

$query = "SELECT SUM(`price`) AS raised FROM transactions_paypal WHERE streamer_id = '$id'";
$res = @mysqli_query($connection, $query);
$sql = mysqli_fetch_array($res);
$raised = $sql['raised'];

?>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="utf-8">
        <meta http-equiv="X-UA-Compatible" content="IE=edge">
        <meta name="viewport" content="width=device-width, initial-scale=1">

        <title>PlayLive</title>
    </head>
<?php
include 'includes/header.php';

if (!isset($_GET['streamer'])) {
    ?>
    <body>
        <div class="container-fluid margintop">
            <div class="row">
                <div class="col-md-8">
                    <div class="card">
                        <h2>The Streamers</h2>
                        <p>Below you can browse all the different streamers streaming the PlayLive event. For information on how to donate to help the cause please click <a href="#">here</a>.</p>
                    </div>
                </div>
                <div class="col-md-4">

                    <div class="card">
                        <h2>Random Streamer</h2>
                        <div class="media">
                            <div class="media-left">
                                <a href="<?php echo $link; ?>">
                                    <img class="media-object" src="https://crafatar.com/avatars/<?php echo $name?>?size=80">
                                </a>
                            </div>
                            <div class="media-body">
                                <h4 class="media-heading"><?php echo $name; ?>'s Stream <strong>$<?php echo $raised; ?></strong></h4>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <div class="row streamers">
                <?php
                    $SQL = "SELECT * FROM `streamers`;";
                    $SQL = @mysqli_query($connection, $SQL);
                    $i = 0;
                    while ($row = mysqli_fetch_array($SQL)) {
                        $id = $row['id'];
                        $link = $row['link'];
                        $name = $row['ign'];

                        if ($i % 4 == 0) {
                            echo "</div>
                            <div class='row streamers'>
                                <div class='col-md-3'>
                                    <div class='card'>
                                        <div class='media'>
                                            <div class='media-left'>
                                                <a href='$link'>
                                                    <img class='media-object' src='https://crafatar.com/avatars/$name?size=80'>
                                                </a>
                                            </div>
                                            <div class='media-body'>
                                                <h4 class='media-heading'><strong>$name</strong></h4>
                                                <p><button type='button' class='btn btn-primary view'><a href='$link'>View Stream</a></button><button type='button' class='btn btn-primary'><a href='?streamer=$id'>Donate</a></button></p>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            ";
                        } else {
                            echo "<div class='col-md-3'>
                                    <div class='card'>
                                        <div class='media'>
                                            <div class='media-left'>
                                                <a href='$link'>
                                                    <img class='media-object' src='https://crafatar.com/avatars/$name?size=80'>
                                                </a>
                                            </div>
                                            <div class='media-body'>
                                                <h4 class='media-heading'><strong>$name</strong></h4>
                                                <p><button type='button' class='btn btn-primary view'><a href='$link'>View Stream</a></button><button type='button' class='btn btn-primary'><a href='?streamer=$id'>Donate</a></button></p>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            ";
                        }
                        $i++;
                    }
                ?>
            </div>
        </div>
    </body>
    <?php
} else {
    $streamer = $_GET['streamer'];

    $SQL = "SELECT * FROM streamers WHERE id = '$streamer'";
    $SQL = @mysqli_query($connection, $SQL);
    $SQL = mysqli_fetch_array($SQL);
    $name = str_replace("https://www.twitch.tv/", "", $SQL['link']);
    ?>
    <body>
        <script src="http://player.twitch.tv/js/embed/v1.js"></script>
        <div class="stream">
            <div class="container-fluid">
                <div id="{PLAYER_DIV_ID}"></div>
                <script type="text/javascript">
                    var options = {
                        width: 854,
                        height: 480,
                        channel: "{<?php echo $name ?>}"
                        //video: "{VIDEO_ID}"
                    };
                    var player = new Twitch.Player("{PLAYER_DIV_ID}", options);
                    player.setVolume(0.5);
                </script>
            </div>
        </div>

        <div class="container-fluid">
            <div class="page-header">
                <h1><?php echo $name ?></h1>
            </div>
            <div class="row">
                <div class="col-md-4 raised">
                    <h2>Raised</h2>
                    <?php
                        $sum = "SELECT SUM(price) AS price FROM transactions_paypal WHERE streamer_id = '$streamer';";
                        $sum = @mysqli_query($connection, $sum);
                        $sum = mysqli_fetch_array($sum);
                    ?>
                    <p>This streamer has raised a total of:</p>
                    <p class="largenumb"><?php echo $sum['price'] ?></p>
                    <p>Thank you to everyone who has donated so far, we really appreciate it!</p>
                </div>
                <div class="col-md-8"><h2>Streamer Message</h2>
                    <p>
                        Lorem ipsum dolor sit amet, consectetur adipiscing elit. Fusce ultrices quam id tincidunt mattis.
                        Nunc et ultricies neque. Suspendisse nec tortor neque. Fusce imperdiet nunc ut libero cursus, nec
                        porta leo finibus. Phasellus vehicula eleifend leo, lobortis convallis risus malesuada a. Fusce
                        tincidunt leo mi, in porttitor justo lacinia vel. Fusce lobortis massa maximus erat porttitor, et
                        lobortis mi facilisis.
                    </p>
                    <p>
                        Donec maximus vel lorem in egestas. Sed luctus commodo ipsum, at egestas mi dapibus sodales.
                    </p>
                </div>
            </div>
            <br/>
            <div class="container-fluid">
                <div class="col-sm-2">
                    <nav class="nav-sidebar">
                        <ul class="nav tabs">
                            <li class="active"><a href="#positive" data-toggle="tab">Positive</a></li>
                            <li class=""><a href="#negative" data-toggle="tab">Negative</a></li>
                            <li class=""><a href="#global" data-toggle="tab">Global</a></li>
                        </ul>
                    </nav>
                </div>
                <div class="tab-content">
                    <div class="tab-pane active text-style" id="positive">
                        <?php
                            $SQL = "SELECT * FROM items WHERE category = '1';";
                            $SQL = @mysqli_query($connection, $SQL);
                        ?>
                        <h2>Positive</h2>
                        <p>
                            Below you can see all of the packages that have a <strong>positive</strong> effect.
                        </p>
                        <br/>
                        <div class="row">
                            <?php
                            $i = 0;
                            while ($row = mysqli_fetch_array($SQL)) {
                                $title = $row['name'];
                                $desc = $row['description'];
                                $price = $row['price'];
                                $id = $row['id'];

                                if ($i % 4 == 0) {
                                    echo "</div>
                                        <div class='row'>
                                            <div class='col-md-3'>
                                                <div class='panel panel-default'>
                                                    <div class='panel-heading'>
                                                        <h4 class='panel-title'>$title</h4>
                                                    </div>                                       
                                                    <div class='panel-body'>
                                                        $desc
                                                        <br/>
                                                        <strong>$price</strong>
                                                    </div>
                                                    <button type='button' class='btn btn-primary'><a href='/store/order.php?category=3&product=$id&streamer=$streamer'>Donate Now!</a></button>
                                                </div>
                                            </div>";
                                } else {
                                    echo "<div class='col-md-3'>
                                            <div class='panel panel-default'>
                                                <div class='panel-heading'>
                                                    <h4 class='panel-title'>$title</h4>
                                                </div>                                       
                                                <div class='panel-body'>
                                                    $desc
                                                    <br/>
                                                    <strong>$price</strong>
                                                </div>
                                                <button type='button' class='btn btn-primary'><a href='/store/order.php?category=3&product=$id&streamer=$streamer'>Donate Now!</a></button>
                                            </div>
                                        </div>";
                                }
                                $i++;
                            }
                            ?>
                        </div>
                    </div>
                    <div class="tab-pane text-style" id="negative">
                        <?php
                            $SQL = "SELECT * FROM items WHERE category = '2';";
                            $SQL = @mysqli_query($connection, $SQL);
                        ?>
                        <h2>Negative</h2>
                        <p>
                            Below you can see all of the packages that have a <strong>negative</strong> effect.
                        </p>
                        <br/>
                        <div class="row">
                            <div class="row">
                                <?php
                                $i = 0;
                                while ($row = mysqli_fetch_array($SQL)) {
                                    $title = $row['name'];
                                    $desc = $row['description'];
                                    $price = $row['price'];

                                    if ($i % 4 == 0) {
                                        echo "</div><div class='row'>
                                        <div class='col-md-3'>
                                            <div class='panel panel-default'>
                                                <div class='panel-heading'>
                                                    <h4 class='panel-title'>$title</h4>
                                                </div>                                       
                                                <div class='panel-body'>
                                                    $desc
                                                    <br/>
                                                    <strong>$price</strong>
                                                </div>
                                                <button type='button' class='btn btn-primary'><a href='/store/order.php?category=3&product=$id&streamer=$streamer'>Donate Now!</a></button>
                                            </div>
                                        </div>";
                                    } else {
                                        echo "<div class='col-md-3'>
                                            <div class='panel panel-default'>
                                                <div class='panel-heading'>
                                                    <h4 class='panel-title'>$title</h4>
                                                </div>                                       
                                                <div class='panel-body'>
                                                    $desc
                                                    <br/>
                                                    <strong>$price</strong>
                                                </div>
                                                <button type='button' class='btn btn-primary'><a href='/store/order.php?category=3&product=$id&streamer=$streamer'>Donate Now!</a></button>
                                            </div>
                                        </div>";
                                    }
                                    $i++;
                                }
                                ?>
                            </div>
                        </div>
                    </div>
                    <div class="tab-pane text-style" id="global">
                        <?php
                        $SQL = "SELECT * FROM items WHERE category = '3';";
                        $SQL = @mysqli_query($connection, $SQL);
                        ?>
                        <h2>Global</h2>
                        <p>
                            Below you can see all of the packages that have a <strong>global</strong> effect.
                        </p>
                        <br/>
                        <div class="row">
                            <?php
                            $i = 0;
                            while ($row = mysqli_fetch_array($SQL)) {
                                $title = $row['name'];
                                $desc = $row['description'];
                                $price = $row['price'];

                                if ($i % 4 == 0) {
                                    echo "</div><div class='row'>
                                            <div class='col-md-3'>
                                                <div class='panel panel-default'>
                                                    <div class='panel-heading'>
                                                        <h4 class='panel-title'>$title</h4>
                                                    </div>                                       
                                                    <div class='panel-body'>
                                                        $desc
                                                        <br/>
                                                        <strong>$price</strong>
                                                    </div>
                                                    <button type='button' class='btn btn-primary'><a href='/store/order.php?category=3&product=$id&streamer=$streamer'>Donate Now!</a></button>
                                                </div>
                                            </div>";
                                } else {
                                    echo "<div class='col-md-3'>
                                                <div class='panel panel-default'>
                                                    <div class='panel-heading'>
                                                        <h4 class='panel-title'>$title</h4>
                                                    </div>                                       
                                                    <div class='panel-body'>
                                                        $desc
                                                        <br/>
                                                        <strong>$price</strong>
                                                    </div>
                                                    <button type='button' class='btn btn-primary'><a href='/store/order.php?category=3&product=$id&streamer=$streamer'>Donate Now!</a></button>
                                                </div>
                                            </div>";
                                }
                                $i++;
                            }
                            ?>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </body>
    <?php
}
?>
    <?php include 'includes/footer.php'; ?>
</html>
