import React, {Component} from "react";
import IconButton from "@material-ui/core/IconButton/IconButton";
import DescriptionIcon from "@material-ui/icons/Description";
import SettingsIcon from "@material-ui/icons/Settings";
import InputWrapper from "./InputWrapper";
import Tooltip from "@material-ui/core/Tooltip/Tooltip";

class DescriptionAndSettingsItemComponent extends Component {

    render() {
        return (
            <InputWrapper>
                {this.props.header}
                <IconButton aria-label={this.props.descriptionInfo}
                            onClick={this.props.getDescriptionActionHandler}>
                    <Tooltip title={this.props.descriptionInfo}>
                        <DescriptionIcon/>
                    </Tooltip>
                </IconButton>
                <IconButton aria-label={this.props.settingsInfo}
                            onClick={this.props.getSettingsActionHandler}>
                    <Tooltip title={this.props.settingsInfo}>
                        <SettingsIcon/>
                    </Tooltip>
                </IconButton>
            </InputWrapper>
        );
    }
}

export default DescriptionAndSettingsItemComponent;
