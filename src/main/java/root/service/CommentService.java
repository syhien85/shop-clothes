package root.service;

import root.dto.CommentDTO;
import root.dto.PageDTO;
import root.dto.SearchCommentDTO;
import root.entity.Comment;
import root.exception.ResourceNotFoundException;
import root.repository.CommentRepo;
import root.repository.ProductRepo;
import root.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@RequiredArgsConstructor
@Service
public class CommentService {

    private final CommentRepo commentRepo;
    private final ProductRepo productRepo;
    private final UserRepo userRepo;

    @Transactional
    public void create(CommentDTO commentDTO) {
        if (!productRepo.existsById(commentDTO.getProduct().getId())) {
            throw new ResourceNotFoundException(
                "comment with product id [%s] already taken".formatted(commentDTO.getProduct().getId())
            );
        }
        if (!userRepo.existsById(commentDTO.getUser().getId())) {
            throw new ResourceNotFoundException(
                "comment with user id [%s] already taken".formatted(commentDTO.getUser().getId())
            );
        }
        //  if comment parent id not exists && parentId != null => ResourceNotFoundException
        if (commentDTO.getParentId() != null && !commentRepo.existsById(commentDTO.getParentId())) {
            throw new ResourceNotFoundException(
                "comment with id [%s] already taken".formatted(commentDTO.getUser().getId())
            );
        }
        Comment comment = new ModelMapper().map(commentDTO, Comment.class);
        commentRepo.save(comment);
    }

    @Transactional
    public void update(CommentDTO commentDTO) {
        Comment currentComment = commentRepo.findById(commentDTO.getId()).orElseThrow(
            () -> new ResourceNotFoundException(
                "comment with id [" + commentDTO.getId() + "] not found"
            )
        );

        currentComment.setStatus(commentDTO.getStatus());
        currentComment.setContent(commentDTO.getContent());
        currentComment.setRate(commentDTO.getRate());

        commentRepo.save(currentComment);
    }

    @Transactional
    public void delete(Long id) {
        if (!commentRepo.existsById(id)) {
            throw new ResourceNotFoundException("comment with id [" + id + "] not found");
        }
        commentRepo.deleteById(id);
    }

    public CommentDTO getById(Long id) {
        return commentRepo.findById(id).map(this::convert).orElseThrow(
            () -> new ResourceNotFoundException("comment with id [" + id + "] not found")
        );
    }

    public PageDTO<CommentDTO> searchService(SearchCommentDTO searchDTO) {
        Sort sortBy = Sort.by("id").descending();

        if (searchDTO.getSortedField() != null && !searchDTO.getSortedField().isEmpty()) {
            sortBy = Sort.by(searchDTO.getSortedField()).ascending();
        }

        PageRequest pageRequest = PageRequest.of(
            searchDTO.getCurrentPage(), searchDTO.getSize(), sortBy
        );

        String key = "%" + searchDTO.getKeyword() + "%";

        Long c1 = (searchDTO.getProductId() != null) ? searchDTO.getProductId() : null;
        Long c2 = searchDTO.getUserId();
        Date c3 = searchDTO.getStart();
        Date c4 = searchDTO.getEnd();

        Page<Comment> page = commentRepo.findAll(pageRequest);

        if (c1 == null && c2 == null && c3 == null && c4 == null) {
            page = commentRepo.searchBy(key, pageRequest);
        }
        if (c1 != null && c2 == null && c3 == null && c4 == null) {
            page = commentRepo.searchByC1(key, c1, pageRequest);
        }
        if (c1 == null && c2 != null && c3 == null && c4 == null) {
            page = commentRepo.searchByC2(key, c2, pageRequest);
        }
        if (c1 == null && c2 == null && c3 != null && c4 == null) {
            page = commentRepo.searchByC3(key, c3, pageRequest);
        }
        if (c1 == null && c2 == null && c3 == null && c4 != null) {
            page = commentRepo.searchByC4(key, c4, pageRequest);
        }
        if (c1 != null && c2 != null && c3 == null && c4 == null) {
            page = commentRepo.searchByC1AndC2(key, c1, c2, pageRequest);
        }
        if (c1 != null && c2 == null && c3 != null && c4 == null) {
            page = commentRepo.searchByC1AndC3(key, c1, c3, pageRequest);
        }
        if (c1 != null && c2 == null && c3 == null && c4 != null) {
            page = commentRepo.searchByC1AndC4(key, c1, c4, pageRequest);
        }
        if (c1 == null && c2 != null && c3 != null && c4 == null) {
            page = commentRepo.searchByC2AndC3(key, c2, c3, pageRequest);
        }
        if (c1 == null && c2 != null && c3 == null && c4 != null) {
            page = commentRepo.searchByC2AndC4(key, c2, c4, pageRequest);
        }
        if (c1 == null && c2 == null && c3 != null && c4 != null) {
            page = commentRepo.searchByC3AndC4(key, c3, c4, pageRequest);
        }
        if (c1 != null && c2 != null && c3 != null && c4 == null) {
            page = commentRepo.searchByC1AndC2AndC3(key, c1, c2, c3, pageRequest);
        }
        if (c1 != null && c2 != null && c3 == null && c4 != null) {
            page = commentRepo.searchByC1AndC2AndC4(key, c1, c2, c4, pageRequest);
        }
        if (c1 != null && c2 == null && c3 != null && c4 != null) {
            page = commentRepo.searchByC1AndC3AndC4(key, c1, c3, c4, pageRequest);
        }
        if (c1 == null && c2 != null && c3 != null && c4 != null) {
            page = commentRepo.searchByC2AndC3AndC4(key, c2, c3, c4, pageRequest);
        }
        if (c1 != null && c2 != null && c3 != null && c4 != null) {
            page = commentRepo.searchByC1AndC2AndC3AndC4(key, c1, c2, c3, c4, pageRequest);
        }

        return PageDTO.<CommentDTO>builder()
            .totalPage(page.getTotalPages())
            .totalElements(page.getTotalElements())
            .data(page.get().map(this::convert).toList())
            .build();
    }

    private CommentDTO convert(Comment comment) {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        return modelMapper.map(comment, CommentDTO.class);
    }
}
