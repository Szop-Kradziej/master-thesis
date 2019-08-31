import UploadAndDownloadItemComponent from "../../../../utils/UploadAndDownloadItemComponent";
import UploadTestCaseFileDialog from "./UploadTestCaseFileDialog";
import React, {Component} from "react";
import * as Api from "../../../../Api";

class ParametersComponent extends Component {

    constructor(props) {
        super(props);
        this.state = {isAddParametersTestCaseFileVisible: false}
    }

    handleDownloadParametersFile = () => {
        this.downloadFile("parameters");
    };

    downloadFile = (fileType) => {
        Api.downloadIntegrationTestCaseFile(this.props.projectName, this.props.integrationName, this.props.testCaseName, fileType)
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
                    header={this.props.parameters ? this.props.parameters : 'Brak'}
                    uploadInfo="Załaduj plik z parametrami"
                    uploadActionHandler={this.handleOpenAddParametersTestCaseFileDialog}
                    downloadInfo="Pobierz plik z parametrami"
                    downloadDisabled={this.props.parameters === null}
                    downloadActionHandler={this.handleDownloadParametersFile}/>
                <UploadTestCaseFileDialog
                    isOpen={this.state.isAddParametersTestCaseFileVisible}
                    closeActionHandler={this.handleCloseAddParametersTestCaseFileDialog}
                    successActionHandler={this.props.integrationChangedHandler}
                    projectName={this.props.projectName}
                    integrationName={this.props.integrationName}
                    testCaseName={this.props.testCaseName}
                    fileType="parameters"
                    headerText="Dodaj parametry uruchomienia"/>
            </div>
        )
    }
}

export default ParametersComponent