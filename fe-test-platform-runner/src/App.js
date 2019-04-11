import React, { Component } from 'react';
import Button from '@material-ui/core/Button';
import {withStyles} from "@material-ui/core";
import {Link} from "react-router-dom";
import classNames from 'classnames';
import {styles} from "./styles/AppStyles";
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

export default withStyles(styles)(App);
