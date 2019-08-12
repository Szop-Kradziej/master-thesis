import React, {Component} from 'react';
import {withStyles} from "@material-ui/core";
import {styles} from "./styles/AppStyles";
import backendUrl from "./backendUrl";

class Auth extends Component {

    constructor(props) {
        super(props);
        this.state = {loginResponse: {}};
    }

    componentDidMount() {
        this.handleLogIn()
    }

    handleLogIn = () => {
        var data = `{"code": "${this.props.location.search.substring(6, this.props.location.search.length)}"}`;

        return fetch(backendUrl(`/login`), {
            method: "POST",
            credentials: "include",
            headers: {'Content-Type': 'application/json'},
            body: data
        }).then(response => response.json())
            .then(json => {
                localStorage.setItem('token', json.token);
                localStorage.setItem('userName', json.userName);
                console.log("Token from storage: " + localStorage.getItem('token') + " userName:" + localStorage.getItem("userName"))
                this.goToPage(json.accessRights)
            });
    };

    goToPage = (accessRight) => {
        if (accessRight === "lecturer") {
            window.location = '/lecturer'
        } else {
            window.location = '/student/projects'
        }
    };

    render() {
        return (
            <div className={this.props.classes.app}>
                <header className={this.props.classes.header}>
                    <p>
                        Trwa logowanie. Proszę czekać.
                    </p>
                </header>
                <body className={this.props.classes.body}>
                {this.props.location.search.substring(6, this.props.location.search.length)}
                </body>

            </div>
        );
    }
}

export default withStyles(styles)(Auth);
