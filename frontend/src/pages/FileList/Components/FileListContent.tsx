/** @jsx jsx */
import { jsx } from "@emotion/core"
import * as FileListContentStyle from "./FileListContentStyle"
import { Button } from 'antd';
import { ButtonShape } from 'antd/lib/button';
import { SizeType } from 'antd/lib/config-provider/SizeContext';
import { useState, useEffect } from "react"

interface HeadBarParameter {
    listHeight: number
    fileList: Array<String>
}

interface FileItemUI {
    fileItem: String
    playShape: ButtonShape | undefined
}

type CountFileItemHeightFunc = { count(listHeight: number): [number, SizeType] }["count"]

const countFileItemHeight: CountFileItemHeightFunc = (listHeight: number) => {
    const eachLine = listHeight / 10
    return (eachLine > 50) ? [50, "large"] : (eachLine > 30 ? [eachLine, "middle"] : [eachLine, "small"])
}

export const FileListContent = (param: HeadBarParameter) => {
    const [playShapes, setPlayShapes] = useState<Array<FileItemUI>>([])
    
    useEffect(() => {
        const initFileItem = param.fileList.map((item, index) => { return { fileItem: item, playShape: undefined } })
        setPlayShapes(initFileItem)
    }, [param.fileList])
    
    const updateItemByIndex = (index: number, item: ButtonShape | undefined) =>
        setPlayShapes(playShapes.map((item1, index1) => index1 === index ? { fileItem: item1.fileItem, playShape: item } : item1))
    
    const comps1 = playShapes.map((eachShape, index) => {
        return (
            <div key={index}>
                <div css={FileListContentStyle.fileListComp(countFileItemHeight(param.listHeight)[0])}>{eachShape.fileItem}</div>
                <div>
                    <Button
                        type="primary"
                        size={countFileItemHeight(param.listHeight)[1]}
                        shape={eachShape.playShape}
                        onMouseEnter={() => updateItemByIndex(index, "round") }
                        onMouseLeave={() => updateItemByIndex(index, undefined) }>
                        立即播放
                    </Button>
                </div>
            </div>
        )
    })

    return (
        <div css={FileListContentStyle.barContent(param.listHeight)}>
            {comps1}
        </div>
    )
}