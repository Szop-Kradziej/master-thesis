import React, {Component} from 'react';
import Button from '@material-ui/core/Button';
import {withStyles} from "@material-ui/core";
import {styles} from "./styles/AppStyles";

class Login extends Component {

    loginHandler = () => {
      console.log("Login")
    };

  render() {
    return (
        <div className={this.props.classes.app}>
            <header className={this.props.classes.header}>
                <p>
                    Panel logowania
                </p>
            </header>
            <body className={this.props.classes.body}>
                    <Button
                        size="large"
                        className={this.props.classes.button}
                        variant="contained"
                        onClick={this.loginHandler}>
                        <a href={"https://github.com/login/oauth/authorize?scope=user:email&client_id=e984173171ad3f4c4133"}>
                            Zaloguj
                        </a>
                    </Button>
            </body>

        </div>
    );
  }
}

export default withStyles(styles)(Login);
