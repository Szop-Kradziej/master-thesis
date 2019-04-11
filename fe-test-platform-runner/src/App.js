import React, { Component } from 'react';
import Button from '@material-ui/core/Button';
import {withStyles} from "@material-ui/core";
import {Link} from "react-router-dom";
import classNames from 'classnames';
class App extends Component {
  render() {
    return (
        <div className={this.props.classes.app}>
            <header className={this.props.classes.header}>
                <p>
                    Panel administracyjny.
                </p>
            </header>
            <body className={this.props.classes.body}>
            <div className={this.props.classes.marginTop}>
                <Link to={"/projects"} className={this.props.classes.link}>
                    <Button size="large" className={classNames(this.props.classes.button, this.props.classes.marginRight)} variant="contained">
                        Zarządzaj projektami
                    </Button>
                </Link>
                <Link to={"/"} className={this.props.classes.link}>
                    <Button size="large" className={this.props.classes.button} variant="contained">
                        Przeglądaj wyniki
                    </Button>
                </Link>
            </div>
            </body>

        </div>
    );
}
}

const styles = () => ({
    app: {
        textAlign: "center",
        backgroundColor: "#e0e0e0",
    },
    header: {
        minHeight: "50vh",
        display: "flex",
        flexDirection: "column",
        alignItems: "center",
        justifyContent: "center",
        fontSize: "calc(10px + 2vmin)",
        color: "black"
    },
    body: {
        minHeight: "50vh",
    },
    button: {
        backgroundColor: "#1fbbb1",
        color: "black"
    },
    link: {
        textDecoration: "none"
    },
    marginTop: {
        marginTop: "10vh"
    },
    marginRight: {
        marginRight: "5vh"
    }

});

export default withStyles(styles)(App);
