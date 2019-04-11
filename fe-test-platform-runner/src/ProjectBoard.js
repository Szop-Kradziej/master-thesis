import React, { Component } from 'react';
import {withStyles} from "@material-ui/core";


class ProjectBoard extends Component {

    render() {
        return (
            <div className={this.props.classes.app}>
                <header className={this.props.classes.header}>
                    <p>
                        Projekt {this.props.match.params.projectId}
                    </p>
                </header>
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
});

export default withStyles(styles)(ProjectBoard);
