package uk.co.samwho.modopticon.storage;

public final class InvalidResourceIdentifierException extends Exception {
  private static final long serialVersionUID = 1165696686023325700L;

  private final String resourceIdentifier;

  public InvalidResourceIdentifierException(String resourceIdentifier) {
    super("invalid resource identifier: " + resourceIdentifier);
    this.resourceIdentifier = resourceIdentifier;
  }

  public String getResourceIdentifier() {
    return resourceIdentifier;
  }
}