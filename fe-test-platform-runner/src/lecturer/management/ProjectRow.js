import React from "react";
import {withStyles} from "@material-ui/core";
import TableRow from "@material-ui/core/TableRow/TableRow";
import {Link} from "react-router-dom";
import {CustomTableCell, styles} from "../../styles/ProjectssBoardStyles";
import * as Api from "../../Api";
import DeleteItemComponent from "../../utils/DeleteItemComponent";

class ProjectRow extends React.Component {

    constructor(props) {
        super(props);
    }

    handleDeleteProject = () => {
        Api.deleteProject(this.props.projectName)
            .then(this.props.projectChangedHandler)
            .catch(function (error) {
                console.log(error);
            });
    };

    render() {
        return (
            <TableRow key={this.props.projectName}>
                <CustomTableCell width="95%" component="th" scope="row">
                    <Link to={`/lecturer/projects/${this.props.projectName}`} className={this.props.classes.link}>
                        {this.props.projectName}
                    </Link>
                </CustomTableCell>
                <CustomTableCell component="th" scope="row">
                    <DeleteItemComponent info="Usuń projekt"
                                         deleteActionHandler={this.handleDeleteProject}/>

                </CustomTableCell>
            </TableRow>
        );
    }
}

export default withStyles(styles)(ProjectRow)