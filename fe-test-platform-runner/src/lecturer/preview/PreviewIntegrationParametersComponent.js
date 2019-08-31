import React, {Component} from "react";
import * as Api from "../../Api";
import DownloadItemComponent from "../../utils/DownloadItemComponent";

class PreviewIntegrationParametersComponent extends Component {

    constructor(props) {
        super(props);
        this.state = {isAddParametersTestCaseFileVisible: false}
    }

    handleDownloadParametersFile = () => {
        Api.downloadIntegrationParametersTestCaseFile(this.props.projectName, this.props.integrationName, this.props.testCaseName, this.props.index)
    };

    render() {
        return (
            <div>
                <DownloadItemComponent header={this.props.index + 1 + ". " + this.props.isParametersPresent ? "parameters" : "Brak"}
                                       info="Pobierz parametry"
                                       disabled={!this.props.isParametersPresent}
                                       downloadActionHandler={this.handleDownloadParametersFile}/>
            </div>
        )
    }
}

export default PreviewIntegrationParametersComponent