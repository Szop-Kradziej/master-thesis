import React, {Component} from 'react';
import {withStyles} from "@material-ui/core";
import {styles} from "../../../styles/ProjectBoardStyles";
import EditItemComponent from "../../../utils/EditItemComponent";
import UploadAndDownloadItemComponent from "../../../utils/UploadAndDownloadItemComponent";

class ProjectHeader extends Component {

    handleEditProjectName = () => {
        //TODO: do action
    };


    handleUploadProjectDescription = () => {
        //TODO: do action
    };

    handleDownloadProjectDescription = () => {
        //TODO: do action
    };

    render() {
        return (
            <div display="block">
                <EditItemComponent
                    header={"Nazwa projektu: " + this.props.projectName}
                    info="Edytuj nazwę projektu"
                    editActionHandler={this.handleEditProjectName}/>
                <UploadAndDownloadItemComponent className={this.props.classes.projectDescription}
                                                header="Opis projektu: project_description.pdf"
                                                uploadInfo="Załaduj opis projektu"
                                                uploadActionHandler={this.handleUploadProjectDescription}
                                                downloadInfo="Pobierz opis projektu"
                                                downloadActionHandler={this.handleDownloadProjectDescription}/>
            </div>
        );
    }
}

export default withStyles(styles)(ProjectHeader);
