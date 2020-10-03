/** @jsx jsx */
import { jsx } from "@emotion/core"
import * as HeadBarStyle from "./HeadBarStyle"

interface HeadBarParameter {
    contentHeight: number
}

const getBarHeight = (bodyHeight: number) => {
    return (bodyHeight > 600) ? 50 : (bodyHeight / 10)
}

const HeadBar = (param: HeadBarParameter) => {
    return (
        <div css={HeadBarStyle.barContent(getBarHeight(param.contentHeight))}>
            影音文件浏览
        </div>
    )
}

export default HeadBar