import React, {Component} from 'react';
import {withStyles} from "@material-ui/core";
import {styles} from "../../../styles/ProjectBoardStyles";
import EditItemComponent from "../../../utils/EditItemComponent";
import UploadAndDownloadItemComponent from "../../../utils/UploadAndDownloadItemComponent";
import * as Api from "../../../Api";
import AddFileDialog from "./UploadProjectDescriptionDialog";

class ProjectHeader extends Component {

    constructor(props) {
        super(props);
        this.state = {
            isAddDescriptionDialogVisible: false,
            inputDescriptionFile: null
        };
    }

    handleEditProjectName = () => {
        //TODO: do action
    };

    handleOpenAddDescriptionDialog = () => {
        this.setState({isAddDescriptionDialogVisible: true});
    };

    handleCloseAddDescriptionDialog = () => {
        this.setState({isAddDescriptionDialogVisible: false});
    };

    handleDownloadProjectDescription = () => {
        Api.downloadProjectDescription(this.props.projectName)

    };

    render() {
        return (
            <div display="block">
                <EditItemComponent
                    header={"Nazwa projektu: " + this.props.projectName}
                    info="Edytuj nazwę projektu"
                    editActionHandler={this.handleEditProjectName}/>
                <UploadAndDownloadItemComponent className={this.props.classes.projectDescription}
                                                header={"Opis projektu: " + (this.props.projectDescription ? this.props.projectDescription : "Brak")}
                                                uploadInfo="Załaduj opis projektu"
                                                uploadActionHandler={this.handleOpenAddDescriptionDialog}
                                                downloadInfo="Pobierz opis projektu"
                                                downloadDisabled={this.props.projectDescription === null}
                                                downloadActionHandler={this.handleDownloadProjectDescription}/>
                <AddFileDialog isOpen={this.state.isAddDescriptionDialogVisible}
                               closeActionHandler={this.handleCloseAddDescriptionDialog}
                               successActionHandler={this.props.projectChangedHandler}
                               projectName={this.props.projectName}
                               headerText="Dodaj opis projektu"/>
            </div>
        );
    }
}

export default withStyles(styles)(ProjectHeader);
