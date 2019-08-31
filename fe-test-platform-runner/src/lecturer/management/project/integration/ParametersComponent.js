import UploadAndDownloadItemComponent from "../../../../utils/UploadAndDownloadItemComponent";
import React, {Component} from "react";
import * as Api from "../../../../Api";
import UploadParametersTestCaseFileDialog from "./UploadParametersTestCaseFileDialog";

class ParametersComponent extends Component {

    constructor(props) {
        super(props);
        this.state = {isAddParametersTestCaseFileVisible: false}
    }

    handleDownloadParametersFile = () => {
        this.downloadFile("parameters");
    };

    downloadFile = (fileType) => {
        Api.downloadIntegrationParametersTestCaseFile(this.props.projectName, this.props.integrationName, this.props.testCaseName, this.props.index)
    };

    handleOpenAddParametersTestCaseFileDialog = () => {
        this.setState({isAddParametersTestCaseFileVisible: true});
    };

    handleCloseAddParametersTestCaseFileDialog = () => {
        this.setState({isAddParametersTestCaseFileVisible: false});
    };

    render() {
        return (
            <div>
                <UploadAndDownloadItemComponent
                    header={this.props.parameters ? (this.props.index + 1) + '. ' + this.props.parameters : (this.props.index + 1) + '. Brak'}
                    uploadInfo="Załaduj plik z parametrami"
                    uploadActionHandler={this.handleOpenAddParametersTestCaseFileDialog}
                    downloadInfo="Pobierz plik z parametrami"
                    downloadDisabled={this.props.parameters === null}
                    downloadActionHandler={this.handleDownloadParametersFile}/>
                <UploadParametersTestCaseFileDialog
                    isOpen={this.state.isAddParametersTestCaseFileVisible}
                    closeActionHandler={this.handleCloseAddParametersTestCaseFileDialog}
                    successActionHandler={this.props.integrationChangedHandler}
                    projectName={this.props.projectName}
                    integrationName={this.props.integrationName}
                    testCaseName={this.props.testCaseName}
                    index={this.props.index}
                    headerText="Dodaj parametry uruchomienia"/>
            </div>
        )
    }
}

export default ParametersComponent