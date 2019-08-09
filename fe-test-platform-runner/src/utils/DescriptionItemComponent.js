import React, {Component} from "react";
import IconButton from "@material-ui/core/IconButton/IconButton";
import DescriptionIcon from "@material-ui/icons/Description";
import InputWrapper from "./InputWrapper";
import Tooltip from "@material-ui/core/Tooltip/Tooltip";

class DescriptionItemComponent extends Component {

    render() {
        return (
            <InputWrapper>
                {this.props.header}
                <IconButton aria-label={this.props.info}
                            onClick={this.props.getDescriptionActionHandler}>
                    <Tooltip title={this.props.info}>
                        <DescriptionIcon/>
                    </Tooltip>
                </IconButton>
            </InputWrapper>
        );
    }
}

export default DescriptionItemComponent;
