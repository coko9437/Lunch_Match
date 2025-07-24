package com.example.kmj.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QBoard is a Querydsl query type for Board
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QBoard extends EntityPathBase<Board> {

    private static final long serialVersionUID = 189179314L;

    public static final QBoard board = new QBoard("board");

    public final DateTimePath<java.time.LocalDateTime> blindDate = createDateTime("blindDate", java.time.LocalDateTime.class);

    public final StringPath blindReason = createString("blindReason");

    public final StringPath content = createString("content");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final BooleanPath isBlinded = createBoolean("isBlinded");

    public final StringPath originalContent = createString("originalContent");

    public final StringPath originalTitle = createString("originalTitle");

    public final StringPath title = createString("title");

    public final StringPath writer = createString("writer");

    public QBoard(String variable) {
        super(Board.class, forVariable(variable));
    }

    public QBoard(Path<? extends Board> path) {
        super(path.getType(), path.getMetadata());
    }

    public QBoard(PathMetadata metadata) {
        super(Board.class, metadata);
    }

}

